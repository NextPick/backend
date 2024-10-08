package com.nextPick.solves.repository;

import com.nextPick.member.entity.Member;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.solves.entity.Solves;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SolvesRepository extends JpaRepository<Solves, Long> {

    Optional<Solves> findByMemberAndQuestionList(Member member,QuestionList questionList);

    @Query("SELECT b FROM solves b JOIN b.questionList q WHERE "
            + "(:questionCategoryId IS NULL OR q.questionCategory.id = :questionCategoryId) AND "
            + "b.correct = :correct AND "
            + "(:keyword IS NULL OR q.question LIKE %:keyword%) AND "
            + "b.member = :member")
    Page<Solves> findByManyFilterAndSortCorrectPercent(
            @Param("questionCategoryId") Long questionCategoryId,
            @Param("keyword") String keyword,
            @Param("correct") boolean correct,
            @Param("member") Member member,
            Pageable pageable);
}
