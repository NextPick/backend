package com.nextPick.feedbacks.mentor.repository;

import com.nextPick.feedbacks.mentor.entity.Mentor;
import com.nextPick.member.entity.Member;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    @NotNull
    Page<Mentor> findAll(@NotNull Pageable pageable);

    Page<Mentor> findAllByMentor(Member mentor, Pageable pageable);

    Optional<Mentor> findByMenteeAndMentorFeedbackId(Member mentee, long mentorFeedbackId);

    Optional<Mentor> findByMenteeAndMentor(Member mentee, Member mentor);
}
