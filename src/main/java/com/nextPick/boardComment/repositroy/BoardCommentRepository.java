package com.nextPick.boardComment.repositroy;


import com.nextPick.board.entity.Board;
import com.nextPick.boardComment.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    List<BoardComment> findByBoard(Board board);
    long countByBoard(Board board);
}
