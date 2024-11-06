package com.nextPick.report.repository;

import com.nextPick.member.entity.Member;
import com.nextPick.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReporterAndRespondent(Member reporter, Member respondent);
    Optional<Report> findByReporterAndReportId(Member reporter, long reportId);
    Page<Report> findByRespondent_Type(Member.memberType type, Pageable pageable);
}
