package com.nextPick.questionCategory.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nextPick.questionList.entity.QuestionList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "question_category")
@Getter
@Setter
@NoArgsConstructor
public class QuestionCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionCategoryId;

    @Column(name = "categoryName", nullable = false)
    private String categoryName;

//    @OneToOne
//    @JsonManagedReference
//    private QuestionList questionList;
}
