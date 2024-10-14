package com.nextPick.questionList.repository;

import com.nextPick.questionCategory.entity.QuestionCategory;
import com.nextPick.questionList.entity.QuestionList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
@EnableJpaRepositories(basePackages = "com.nextPick.questionList.repository")
public interface QuestionListRepository extends JpaRepository<QuestionList, Long> {

    Optional<QuestionList> findByQuestion(String questionList);

    @Query("SELECT b FROM question_list b WHERE "
            + "(:questionCategoryId IS NULL OR b.questionCategory.id = :questionCategoryId) AND "
            + "(:keyword IS NULL OR b.question LIKE %:keyword%)")
    Page<QuestionList> findByManyFilter(@Param("questionCategoryId") Long questionCategoryId,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);


    @Query(value = "SELECT * FROM question_list WHERE question_category_id = :QuestionCategoryId "
            + "ORDER BY RAND() LIMIT :limitValue", nativeQuery = true)
    List<QuestionList> findRandomQuestionsByCategory(@Param("QuestionCategoryId") long questionCategoryId,
                                                     @Param("limitValue") int limitValue);

}
