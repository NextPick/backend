package com.nextPick.solves.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nextPick.audit.Auditable;
import com.nextPick.member.entity.Member;
import com.nextPick.questionCategory.entity.QuestionCategory;
import com.nextPick.questionList.entity.QuestionList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "solves")
@Getter
@Setter
@NoArgsConstructor
public class Solves extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long solvesId;

    @Column(name = "correct", nullable = false)
    private boolean correct;

    @Column(name = "my_answer", nullable = false)
    private String myAnswer;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "question_id") // 외래 키 컬럼 이름 지정
    private QuestionList questionList;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "member") // 외래 키 컬럼 이름 지정
    private Member member;
}
