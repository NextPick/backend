package com.nextPick.boardComment.service;

import com.nextPick.board.entity.Board;
import com.nextPick.board.repository.BoardRepository;
import com.nextPick.boardComment.entity.BoardComment;
import com.nextPick.boardComment.repositroy.BoardCommentRepository;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.member.service.MemberService;
import com.nextPick.utils.ExtractMemberAndVerify;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class BoardCommentService extends ExtractMemberAndVerify {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository repository;

    public BoardCommentService(BoardCommentRepository boardCommentRepository, BoardRepository boardRepository, MemberService memberService, MemberRepository repository) {
        this.boardCommentRepository = boardCommentRepository;
        this.boardRepository = boardRepository;
        this.repository = repository;
    }

    public BoardComment createBoardComment(BoardComment boardComment, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        Member member = extractMemberFromPrincipal(repository);
        boardComment.setBoard(board);
        boardComment.setMember(member);
        if (boardComment.getParentComment() != null) {
            BoardComment parentComment = boardCommentRepository.findById(boardComment.getParentComment().getBoardCommentId())
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
            boardComment.setParentComment(parentComment);
        }

        return boardCommentRepository.save(boardComment);
    }

    public BoardComment updateBoardComment(Long boardCommentId, BoardComment boardComment) {
        BoardComment existingComment = boardCommentRepository.findById(boardCommentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        Member member = extractMemberFromPrincipal(repository);
        if (!Objects.equals(existingComment.getMember().getMemberId(), member.getMemberId())) {
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

    public void deleteBoardComment(Long boardCommentId) {
        BoardComment boardComment = boardCommentRepository.findById(boardCommentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        Member member = extractMemberFromPrincipal(repository);
        if (!Objects.equals(boardComment.getMember().getMemberId(), member.getMemberId())) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_ACTION);
        }
        boardCommentRepository.delete(boardComment);
    }
}
