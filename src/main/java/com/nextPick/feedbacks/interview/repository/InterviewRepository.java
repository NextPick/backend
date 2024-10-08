package com.nextPick.feedbacks.interview.repository;

import com.nextPick.feedbacks.interview.entity.Interview;
import com.nextPick.member.entity.Member;
import com.nextPick.report.entity.Report;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    @NotNull
    Page<Interview> findAll(@NotNull Pageable pageable);

    Page<Interview> findAllMentee(Member mentee, Pageable pageable);

//    Optional<Interview> findByMenteeAndInterviewFeedbackId(Member mentee, long interviewFeedbackId);
}
