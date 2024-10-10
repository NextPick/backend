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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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


    public List<BoardDto.Response> getBoardsByDtype(String dtype) {
        logger.info("Fetching boards of type: {}", dtype);
        List<Board> boards = boardRepository.findAllByDtype(dtype);
        logger.info("Found {} boards of type {}", boards.size(), dtype);
        return boardMapper.boardsToResponses(boards);
    }


    // 게시글 생성
//    public BoardDto.Response createBoard(BoardDto.Post postDto) {
//        Member member = extractMemberFromPrincipal(memberRepository);
//        Board board = boardMapper.postDtoToBoard(postDto);
//        board.setMember(member);
//        board.setMemberNickname(member.getNickname());
//        Board savedBoard = boardRepository.save(board);
//        return boardMapper.boardToResponse(savedBoard);
//    }


    public BoardDto.Response createBoard(BoardDto.Post postDto, String dtype) {
        Member member = extractMemberFromPrincipal(memberRepository);

        // dtype에 따라 QuestionBoard 또는 ReviewBoard로 변환
        Board board;
        if ("Q".equals(dtype)) {
            board = new QuestionBoard();  // 여기서 직접 객체 생성
        } else if ("R".equals(dtype)) {
            board = new ReviewBoard();  // 여기서 직접 객체 생성
        } else {
            throw new BusinessLogicException(ExceptionCode.INVALID_BOARD_TYPE);
        }

        // postDto를 이용해 board 객체에 값 매핑
        boardMapper.postDtoToBoard(postDto, board);
        board.setMember(member);
        board.setMemberNickname(member.getNickname());


        Board savedBoard = boardRepository.save(board);
        return boardMapper.boardToResponse(savedBoard);
    }



    // 게시글 수정
    @Transactional
    public BoardDto.Response updateBoard(Long boardId, BoardDto.Patch patchDto) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Board board = findVerifiedBoard(boardId);
        validateBoardOwnership(board, member);
        boardMapper.patchDtoToBoard(patchDto, board);
        Board updatedBoard = boardRepository.save(board);
        return boardMapper.boardToResponse(updatedBoard);
    }

    // 좋아요 토글
    public void toggleLike(Long boardId) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Board board = findVerifiedBoard(boardId);

        Optional<BoardLike> existingLike = boardLikeRepository.findByMemberAndBoard(member, board);

        if (existingLike.isPresent()) {
            boardLikeRepository.delete(existingLike.get());
            board.setLikesCount(board.getLikesCount() - 1);
        } else {
            BoardLike newLike = new BoardLike();
            newLike.setBoard(board);
            newLike.setMember(member);
            boardLikeRepository.save(newLike);
            board.setLikesCount(board.getLikesCount() + 1);
        }

        boardRepository.save(board);
    }

    // 게시글 삭제
    public void deleteBoard(long boardId) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Board board = findVerifiedBoard(boardId);
        validateBoardOwnership(board, member);
        board.setBoardStatus(Board.BoardStatus.BOARD_DELETED);
        boardRepository.save(board);
    }

    // 조회수 증가 후 게시글 가져오기
    public BoardDto.Response getBoardAndIncrementViewCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));

        // 게시글이 삭제 상태인 경우 예외 처리
        if (board.getBoardStatus() == Board.BoardStatus.BOARD_DELETED) {
            throw new BusinessLogicException(ExceptionCode.BOARD_DELETED); // 커스텀 예외 처리
        }

        board.incrementViewCount();
        boardRepository.save(board);  // 변경된 조회수를 저장

        return boardMapper.boardToResponse(board);
    }

    // 게시글 검증
    private Board findVerifiedBoard(long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
    }

    // 게시글 소유권 검증
    private void validateBoardOwnership(Board board, Member member) {
        if (board.getMember() == null || member == null ||
                !Objects.equals(board.getMember().getMemberId(), member.getMemberId())) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_MEMBER);
        }
    }
}