package com.nextPick.questionCategory.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class QuestionCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionCategoryId;

    @Column(name = "categoryName", nullable = false)
    private String categoryName;

}
