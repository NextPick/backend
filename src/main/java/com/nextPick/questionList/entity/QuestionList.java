package com.nextPick.questionList.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "question_list")
@Getter
@Setter
@NoArgsConstructor
public class QuestionList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionListId;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Setter
    @Column(name = "correct_count", nullable = false)
    private int correctCount = 0;

    @Column(name = "wrong_count", nullable = false)
    private int wrongCount = 0;

    @Column(name = "correct_rate", nullable = false)
    private int correctRate = 0;
}
