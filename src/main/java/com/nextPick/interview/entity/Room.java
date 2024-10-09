package com.nextPick.interview.entity;

import javax.persistence.*;

import com.nextPick.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String uuid = UUID.randomUUID().toString();

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "room")
    private List<Participant> participants;

    @Column
    private String sessionId;

    @Enumerated(value = EnumType.STRING)
    @Column
    private roomOccupation occupation;

    @Getter
    public enum roomOccupation {
        BE("백엔드"),
        FE("프론트엔드");

        private final String occupation;

        roomOccupation(String occupation) {this.occupation = occupation;}
    }
}
