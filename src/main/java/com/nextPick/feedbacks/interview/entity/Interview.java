package com.nextPick.feedbacks.interview.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nextPick.audit.Auditable;
import com.nextPick.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "interview_feedbacks")
@Getter
@Setter
@NoArgsConstructor
public class Interview extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long interviewFeedbackId;

    @Column(name = "content", length= 255, nullable = false)
    private String content;

    @Column(name = "room_id", nullable = false)
    private long roomId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    @JoinColumn(name = "mentor")
    private Member mentor;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    @JoinColumn(name = "mentee")
    private Member mentee;
}
