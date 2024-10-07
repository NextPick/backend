//package com.nextPick.keyword.repository;
//
//import com.nextPick.keyword.entity.Keyword;
//import com.nextPick.questionList.entity.QuestionList;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface KeywordRepository extends JpaRepository<Keyword, Long> {
//    List<Keyword> findAllByQuestionList(QuestionList questionList);
//
//    void deleteAllByQuestionList(QuestionList questionList);
//}
