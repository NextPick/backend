package com.nextPick.boardLike.respository;

import com.nextPick.board.entity.Board;
import com.nextPick.boardLike.entity.BoardLike;
import com.nextPick.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository <BoardLike, Long> {
    Optional<BoardLike> findByMember(Member member);
    Optional<BoardLike> findByMemberAndBoard(Member member, Board board);
    Optional<BoardLike> findByMember_emailAndBoard(String memberEmail, Board board);
    List<BoardLike> findByMember_email(String memberEmail);

}
