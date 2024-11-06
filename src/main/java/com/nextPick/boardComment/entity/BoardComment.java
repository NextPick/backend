package com.nextPick.boardComment.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nextPick.audit.Auditable;
import com.nextPick.board.entity.Board;
import com.nextPick.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BoardComment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardCommentId;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", nullable = false)
    private Board board;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private BoardComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardComment> replies = new ArrayList<>();

    public void addReply(BoardComment reply) {
        replies.add(reply);
        reply.setParentComment(this);
    }

    public BoardComment(String content, Board board, Member member, BoardComment parent) {
        this.content = content;
        this.board = board;
        this.member = member;
        this.parentComment = parent;
    }

    public BoardComment(Long boardCommentId) {
        this.boardCommentId = boardCommentId;
    }
}
