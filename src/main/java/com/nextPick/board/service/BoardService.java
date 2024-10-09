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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);
    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final MemberRepository memberRepository;
    private final BoardMapper boardMapper;

//    public BoardDto.Response getBoardById(Long boardId) {
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
//        return boardMapper.boardToResponse(board);
//    }

    // 특정 게시판 유형의 게시글 조회
    public List<BoardDto.Response> getBoardsByDtype(String dtype) {
        logger.info("Fetching boards of type: {}", dtype);
        List<Board> boards = boardRepository.findAllByDtype(dtype);
        logger.info("Found {} boards of type {}", boards.size(), dtype);
        return boardMapper.boardsToResponses(boards);
    }


//    public List<BoardDto.Response> getBoardsByDtype(String dtype) {
//        Class<? extends Board> boardType;
//        switch (dtype) {
//            case "Q":
//                boardType = QuestionBoard.class;
//                break;
//            case "R":
//                boardType = ReviewBoard.class;
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid board type: " + dtype);
//        }
//        return getBoardsByType(boardType);
//    }

    @Transactional
    public void incrementViewCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        board.incrementViewCount();
        boardRepository.save(board);
    }

    @Transactional
    public void toggleLike(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

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

    public BoardDto.Response createBoard(BoardDto.Post postDto, Long memberId) {
        // 예외 발생 시 BusinessLogicException을 던짐
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Board board = boardMapper.postDtoToBoard(postDto);
        board.setMember(member);
        board.setMemberNickname(member.getNickname());

        Board savedBoard = boardRepository.save(board);
        return boardMapper.boardToResponse(savedBoard);
    }

    @Transactional
    public BoardDto.Response updateBoard(Long boardId, BoardDto.Patch patchDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        boardMapper.patchDtoToBoard(patchDto, board);
        Board updatedBoard = boardRepository.save(board);

        return boardMapper.boardToResponse(updatedBoard);
    }

    public BoardDto.Response getBoardAndIncrementViewCount(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        // 조회수 증가
        board.incrementViewCount();
        boardRepository.save(board);  // 변경된 조회수를 저장

        return boardMapper.boardToResponse(board);
    }

    public void deleteBoard(long boardId, Authentication authentication){
        extractMemberFromAuthentication(authentication);
        Board findBoard = findVerifiedBoard(boardId);
        if(!findBoard.getMember().getEmail().equals(authentication.getName())) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_MEMBER);
        }
        findBoard.setBoardStatus(Board.BoardStatus.BOARD_DELETED);
        boardRepository.save(findBoard);
    }

    @Transactional(readOnly = true)
    public Board findVerifiedBoard(long boardId){
        Optional<Board> findBoard =
                boardRepository.findById(boardId);
        Board result = findBoard.orElseThrow(()->
                new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        return result;
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() == null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        String email = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }


}