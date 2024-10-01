package com.nextPick.questionList.repository;

import com.nextPick.questionList.entity.QuestionList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionListRepository extends JpaRepository<QuestionList, Long> {
}
