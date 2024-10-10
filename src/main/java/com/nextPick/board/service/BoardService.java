package com.nextPick.board.service;

import com.nextPick.board.dto.BoardDto;
import com.nextPick.board.entity.Board;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService extends ExtractMemberAndVerify {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardMapper boardMapper;

    // 게시글 생성
    public BoardDto.Response createBoard(BoardDto.Post postDto) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Board board = boardMapper.postDtoToBoard(postDto);
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
        Board board = findVerifiedBoard(boardId);
        board.incrementViewCount();
        boardRepository.save(board);
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