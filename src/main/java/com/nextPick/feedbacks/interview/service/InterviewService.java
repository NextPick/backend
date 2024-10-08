package com.nextPick.feedbacks.interview.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.feedbacks.interview.entity.Interview;
import com.nextPick.feedbacks.interview.repository.InterviewRepository;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
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
public class InterviewService extends ExtractMemberAndVerify {
    private final InterviewRepository interviewRepository;
    private final MemberRepository memberRepository;

    public void createInterview(Interview interview, long roomId, long mentorId) {
        Member mentor = extractMemberFromPrincipal(memberRepository);
        Member mentee = memberRepository.findById(mentorId)
                        .orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        interview.setMentee(mentee);
        interview.setMentor(mentor);
        interview.setRoomId(roomId);
        interviewRepository.save(interview);
    }

    public void deleteInterviewForAdmin(long interviewFeedbackId){
        Interview interview = interviewRepository.findById(interviewFeedbackId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.INTERVIEW_NOT_FOUND));
        interviewRepository.delete(interview);
    }

    public Page<Interview> getInterviewPage(int page, int size, boolean adminMode) {
        Pageable pageable = PageRequest.of(page, size);
        if(adminMode){
            return interviewRepository.findAll(pageable);
        }else{
            Member mentee = extractMemberFromPrincipal(memberRepository);
            return interviewRepository.findAllByMentee(mentee,pageable);
        }
    }

}
