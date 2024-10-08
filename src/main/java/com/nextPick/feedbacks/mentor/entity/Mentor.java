package com.nextPick.feedbacks.mentor.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nextPick.audit.Auditable;
import com.nextPick.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity(name = "interview_feedbacks")
@Getter
@Setter
@NoArgsConstructor
public class Mentor extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mentorFeedbackId;

    @Column(name = "room_id", nullable = false)
    private long roomId;

    @Column(name = "content", length= 255, nullable = false)
    private String content;

    @Column(name = "star_rating", nullable = false)
    @Min(0)
    @Max(5)
    private int starRating;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    @JoinColumn(name = "mentor")
    private Member mentor;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    @JoinColumn(name = "mentee")
    private Member mentee;
}
