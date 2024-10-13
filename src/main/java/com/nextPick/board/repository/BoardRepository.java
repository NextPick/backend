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
    @Query("SELECT b FROM Board b WHERE TYPE(b) = QuestionBoard AND b.boardStatus = :boardStatus")
    Page<QuestionBoard> findAllQuestionBoards(@Param("boardStatus") Board.BoardStatus boardStatus, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE TYPE(b) = ReviewBoard AND b.boardStatus = :boardStatus")
    Page<ReviewBoard> findAllReviewBoards(@Param("boardStatus") Board.BoardStatus boardStatus, Pageable pageable);
}