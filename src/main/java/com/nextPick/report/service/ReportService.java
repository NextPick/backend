package com.nextPick.report.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.report.entity.Report;
import com.nextPick.report.repository.ReportRepository;
import com.nextPick.utils.ExtractMemberAndVerify;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService extends ExtractMemberAndVerify {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    public void createReport(Report report) {
        Member reporter = extractMemberFromPrincipal(memberRepository);
        Member respondent = memberRepository.findByNickname(reporter.getNickname())
                        .orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        reportRepository.findByReporterAndRespondent(reporter,respondent)
                .ifPresent(r -> { throw new BusinessLogicException(ExceptionCode.REPORTS_EXISTS);});
        report.setReporter(reporter);
        reportRepository.save(report);
    }

    public void deleteReport(long reportId){
        Member reporter = extractMemberFromPrincipal(memberRepository);
        Report report = reportRepository.findByReporterAndReportId(reporter, reportId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.REPORTS_NOT_FOUND));
        reportRepository.delete(report);
    }

    public Report getReport(long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.REPORTS_NOT_FOUND));
    }

    public Page<Report> getReportPage(int page, int size, String type) {
        Pageable pageable = PageRequest.of(page, size);
        Member.memberType memberType = Member.memberType.MEMTEE;
        if(type.equals("mentor"))
            memberType = Member.memberType.MENTOR;
        return reportRepository.findByRespondent_Type(memberType,pageable);
    }

    public Member memberBan(long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        findMember.setStatus(Member.memberStatus.BAN);
        return memberRepository.save(findMember);
    }

}
