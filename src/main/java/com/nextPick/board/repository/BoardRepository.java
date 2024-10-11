package com.nextPick.board.repository;

import com.nextPick.board.entity.Board;
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

    @Query("SELECT b FROM Board b WHERE TYPE(b) = CASE WHEN :dtype = 'Q' THEN QuestionBoard WHEN :dtype = 'R' THEN ReviewBoard ELSE Board END ")
    Page<Board> findAllByDtype(@Param("dtype") String dtype, Pageable pageable);
}