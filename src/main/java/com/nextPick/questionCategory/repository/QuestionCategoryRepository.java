package com.nextPick.questionCategory.repository;

import com.nextPick.questionCategory.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {
}
