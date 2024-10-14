package com.nextPick.board.repository;

import com.nextPick.board.entity.Board;
import com.nextPick.board.entity.QuestionBoard;
import com.nextPick.board.entity.ReviewBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM ReviewBoard b WHERE b.boardStatus = :status AND " +
            "(:keyword IS NULL OR :keyword = '' OR (b.title LIKE %:keyword% OR b.content LIKE %:keyword%))")
    Page<ReviewBoard> findAllReviewBoardsWithKeyword(@Param("status") Board.BoardStatus status,
                                                     @Param("keyword") String keyword,
                                                     Pageable pageable);

    @Query("SELECT b FROM QuestionBoard b WHERE b.boardStatus = :status AND " +
            "(:keyword IS NULL OR :keyword = '' OR (b.title LIKE %:keyword% OR b.content LIKE %:keyword%))")
    Page<QuestionBoard> findAllQuestionBoardsWithKeyword(@Param("status") Board.BoardStatus status,
                                                         @Param("keyword") String keyword,
                                                         Pageable pageable);




}