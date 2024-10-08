package com.nextPick.questionList.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.nextPick.keyword.entity.Keyword;
import com.nextPick.questionCategory.entity.QuestionCategory;
import com.nextPick.solves.entity.Solves;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "questionList", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Solves> solvesList;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "question_category_id") // 외래 키 컬럼 이름 지정
    private QuestionCategory questionCategory;
}
