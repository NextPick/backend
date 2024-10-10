package com.nextPick.board.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nextPick.audit.Auditable;
import com.nextPick.boardLike.entity.BoardLike;
import com.nextPick.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorColumn( name = "dtype" , discriminatorType = DiscriminatorType.STRING, length = 1)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Board extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Column(nullable = false)
    private String memberNickname;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer likesCount = 0;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @NotNull
    @Column
    @Enumerated(value = EnumType.STRING)
    private BoardStatus boardStatus = BoardStatus.BOARD_POST;

    @AllArgsConstructor
    public enum BoardStatus{
        BOARD_POST("게시글 게시 상태"),
        BOARD_DELETED("게시글 삭제 상태");

        @Getter
        @Setter
        private String statusDescription;
    }

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = {CascadeType.REMOVE})
    @JsonManagedReference
    private List<BoardLike> boardLikeList = new ArrayList<>();

    public void incrementViewCount() {
        this.viewCount++;
    }
}
