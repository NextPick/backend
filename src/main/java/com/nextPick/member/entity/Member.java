package com.nextPick.member.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nextPick.board.entity.Board;
import com.nextPick.boardLike.entity.BoardLike;

import com.nextPick.solves.entity.Solves;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @Column(name = "name", length= 100, nullable = false)
    private String name;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "gender", length = 2, nullable = false)
    private String gender;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Setter
    @Column(name = "nickname", length = 255, nullable = false)
    private String nickname;

    @Column(name = "guilty_score", nullable = false)
    private Integer guiltyScore = 0;

    @Column(name = "occupation", nullable = false)
    private memberOccupation occupation;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private memberStatus status = memberStatus.ACTIVE;

    @JsonManagedReference
    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    @JsonManagedReference
    private List<BoardLike> boardLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Solves> solvesList;

    @Getter
    public enum memberStatus {
        ACTIVE("유효한 회원"),
        DELETED("삭제된 회원"),
        PENDING("보류된 회원");

        private final String status;

        memberStatus(String status) {
            this.status = status;
        }
    }

    @Getter
    public enum memberOccupation {
        BE("백엔드"),
        FE("프론트엔드");

        private final String status;

        memberOccupation(String status) {
            this.status = status;
        }
    }
}
