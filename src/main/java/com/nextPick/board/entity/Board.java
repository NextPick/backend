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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Board extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Column(nullable = false)
    private String memberNickname;

    @Column(nullable = false , length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String dtype;

    @Column(nullable = false)
    private Integer likesCount = 0;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "board",cascade = {CascadeType.REMOVE})
    @JsonManagedReference
    private List<BoardLike> boardLikeList = new ArrayList<>();

    @AllArgsConstructor
    @NoArgsConstructor
    public enum BoardCategory {
        BE("백엔드"),
        FE("프론트엔드"),
        CS("컴퓨터 사이언스");

        @Getter
        @Setter
        private String boardCategory;
    }
    public void incrementViewCount() {
        this.viewCount++;
    }
}
