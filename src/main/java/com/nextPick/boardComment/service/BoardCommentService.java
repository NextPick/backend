package com.nextPick.boardComment.service;

import com.nextPick.board.entity.Board;
import com.nextPick.board.repository.BoardRepository;
import com.nextPick.boardComment.entity.BoardComment;
import com.nextPick.boardComment.repositroy.BoardCommentRepository;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.entity.Member;
import com.nextPick.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardRepository boardRepository;
    private final MemberService memberService;

    public BoardCommentService(BoardCommentRepository boardCommentRepository, BoardRepository boardRepository, MemberService memberService) {
        this.boardCommentRepository = boardCommentRepository;
        this.boardRepository = boardRepository;
        this.memberService = memberService;
    }

    public BoardComment createBoardComment(BoardComment boardComment, Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        Member member = memberService.findVerifiedMember(memberId);
        boardComment.setBoard(board);
        boardComment.setMember(member);
        if (boardComment.getParentComment() != null) {
            BoardComment parentComment = boardCommentRepository.findById(boardComment.getParentComment().getBoardCommentId())
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
            boardComment.setParentComment(parentComment);
        }

        return boardCommentRepository.save(boardComment);
    }

    public BoardComment updateBoardComment(Long boardCommentId, BoardComment boardComment, Long memberId) {
        BoardComment existingComment = boardCommentRepository.findById(boardCommentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        if (!Objects.equals(existingComment.getMember().getMemberId(), memberId)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_ACTION);
        }
        existingComment.setContent(boardComment.getContent());
        return boardCommentRepository.save(existingComment);
    }

    @Transactional(readOnly = true)
    public List<BoardComment> findBoardCommentsByBoardId(long boardId) {
        return boardCommentRepository.findByBoard(
                boardRepository.findById(boardId)
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND))
        );
    }

    public void deleteBoardComment(Long boardCommentId, Long memberId) {
        BoardComment boardComment = boardCommentRepository.findById(boardCommentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        if (!Objects.equals(boardComment.getMember().getMemberId(), memberId)) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_ACTION);
        }
        boardCommentRepository.delete(boardComment);
    }
}
