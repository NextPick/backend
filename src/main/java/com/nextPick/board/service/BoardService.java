package com.nextPick.board.service;

import com.nextPick.board.dto.BoardDto;
import com.nextPick.board.entity.Board;
import com.nextPick.board.entity.QuestionBoard;
import com.nextPick.board.entity.ReviewBoard;
import com.nextPick.board.mapper.BoardMapper;
import com.nextPick.board.repository.BoardRepository;
import com.nextPick.boardLike.entity.BoardLike;
import com.nextPick.boardLike.respository.BoardLikeRepository;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.utils.ExtractMemberAndVerify;
import com.nextPick.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardService extends ExtractMemberAndVerify {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardMapper boardMapper;
    private final S3Uploader s3Uploader;



    public Page<BoardDto.Response> getBoardsByDtype(String dtype, String sort, String keyword, Pageable pageable) {
        Page<? extends Board> boardPage;
        Sort sortBy;
        switch (sort) {
            case "recent":
                sortBy = Sort.by("createdAt").descending();
                break;
            case "likes":
                sortBy = Sort.by("likesCount").descending();
                break; // 이 부분이 누락되어 있었습니다.
            case "views":
                sortBy = Sort.by("viewCount").descending();
                break;
            default:
                throw new IllegalArgumentException("Invalid sort type: " + sort);
        }

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);

        // 키워드가 없거나 "*"인 경우 전체 게시물 조회
        if (keyword == null || keyword.equals("*")) {
            if ("R".equals(dtype)) {
                boardPage = boardRepository.findAllReviewBoards(Board.BoardStatus.BOARD_POST, pageable);
            } else if ("Q".equals(dtype)) {
                boardPage = boardRepository.findAllQuestionBoards(Board.BoardStatus.BOARD_POST, pageable);
            } else {
                throw new IllegalArgumentException("Invalid dtype value");
            }
        } else {
            if ("R".equals(dtype)) {
                boardPage = boardRepository.findAllReviewBoardsWithKeyword(Board.BoardStatus.BOARD_POST, keyword, pageable);
            } else if ("Q".equals(dtype)) {
                boardPage = boardRepository.findAllQuestionBoardsWithKeyword(Board.BoardStatus.BOARD_POST, keyword, pageable);
            } else {
                throw new IllegalArgumentException("Invalid dtype value");
            }
        }

        return boardPage.map(boardMapper::boardToResponse);
    }


    public BoardDto.Response createBoard(BoardDto.Post postDto, String dtype, List<MultipartFile> images) throws IOException {
        Member member = extractMemberFromPrincipal(memberRepository);
        Board board;
        if ("Q".equals(dtype)) {
            board = new QuestionBoard();
        } else if ("R".equals(dtype)) {
            board = new ReviewBoard();
        } else {
            throw new BusinessLogicException(ExceptionCode.INVALID_BOARD_TYPE);
        }

        boardMapper.postDtoToBoard(postDto, board);
        board.setMember(member);
        board.setMemberNickname(member.getNickname());
        board.setBoardId(board.getBoardId());
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            imageUrls = s3Uploader.uploadImages(images);
        }
        board.setImageUrls(imageUrls);
        Board savedBoard = boardRepository.save(board);
        return boardMapper.boardToResponse(savedBoard);
    }




    @Transactional
    public BoardDto.Response updateBoard(Long boardId, BoardDto.Patch patchDto, List<MultipartFile> newImages, List<String> imagesToDelete) throws IOException {
        try {
            Member member = extractMemberFromPrincipal(memberRepository);
            Board board = findVerifiedBoard(boardId);
            validateBoardOwnership(board, member);
            boardMapper.patchDtoToBoard(patchDto, board);

            // 이미지 삭제 처리 (선택적)
            if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
                List<String> currentImages = board.getImageUrls();
                currentImages.removeAll(imagesToDelete);

                for (String imageUrl : imagesToDelete) {
                    System.out.println("이미지 삭제 시도: " + imageUrl);
                    s3Uploader.delete(imageUrl);
                    System.out.println("이미지 삭제 완료: " + imageUrl);
                }
                board.setImageUrls(currentImages);
            }
            // 새 이미지 추가 처리 (선택적)
            if (newImages != null && !newImages.isEmpty()) {
                List<String> currentImages = board.getImageUrls();
                List<String> newImageUrls = s3Uploader.uploadImages(newImages);
                currentImages.addAll(newImageUrls);
                board.setImageUrls(currentImages);
            }

            Board updatedBoard = boardRepository.save(board);
            return boardMapper.boardToResponse(updatedBoard);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND);
        }
    }


    public Map<String, Object> toggleLike(Long boardId) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Board board = findVerifiedBoard(boardId);

        Optional<BoardLike> existingLike = boardLikeRepository.findByMemberAndBoard(member, board);

        boolean likedByUser = false;
        if (existingLike.isPresent()) {
            boardLikeRepository.delete(existingLike.get());
            board.setLikesCount(board.getLikesCount() - 1);
        } else {
            BoardLike newLike = new BoardLike();
            newLike.setBoard(board);
            newLike.setMember(member);
            boardLikeRepository.save(newLike);
            board.setLikesCount(board.getLikesCount() + 1);
            likedByUser = true;  // 유저가 이제 좋아요를 눌렀으므로 true
        }

        boardRepository.save(board);

        // 좋아요 수와 사용자가 현재 좋아요를 눌렀는지 상태 반환
        Map<String, Object> response = new HashMap<>();
        response.put("likesCount", board.getLikesCount());
        response.put("likedByUser", likedByUser);

        return response;
    }


    @Transactional
    public void deleteBoard(long boardId) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Board board = findVerifiedBoard(boardId);
        validateBoardOwnership(board, member);

        if (board.getImageUrls() != null && !board.getImageUrls().isEmpty()) {
            s3Uploader.deleteImages(board.getImageUrls());  // 이미지 URL 리스트를 전달하여 삭제
        }

        board.setBoardStatus(Board.BoardStatus.BOARD_DELETED);
        boardRepository.save(board);
    }

    public BoardDto.Response getBoardAndIncrementViewCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        // 게시글이 삭제 상태인 경우 예외 처리
        if (board.getBoardStatus() == Board.BoardStatus.BOARD_DELETED) {
            throw new BusinessLogicException(ExceptionCode.BOARD_DELETED); // 커스텀 예외 처리
        }

        board.incrementViewCount();
        boardRepository.save(board);

        return boardMapper.boardToResponse(board);
    }

    private Board findVerifiedBoard(long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        if (board.getBoardStatus() == Board.BoardStatus.BOARD_DELETED) {
            throw new BusinessLogicException(ExceptionCode.BOARD_DELETED);
        }

        return board;
    }


    private void validateBoardOwnership(Board board, Member member) {
        if (board.getMember() == null || member == null ||
                !Objects.equals(board.getMember().getMemberId(), member.getMemberId())) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_MEMBER);
        }
    }





}