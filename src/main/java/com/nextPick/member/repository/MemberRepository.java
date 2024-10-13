package com.nextPick.member.repository;

import com.nextPick.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);

    Page<Member> findAllByTypeAndStatus(Member.memberType memberType,
                                        Member.memberStatus memberStatus,
                                        Pageable pageable);
}
