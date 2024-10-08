package com.nextPick.feedbacks.mentor.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.feedbacks.mentor.entity.Mentor;
import com.nextPick.feedbacks.mentor.repository.MentorRepository;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.utils.ExtractMemberAndVerify;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MentorService extends ExtractMemberAndVerify {
    private final MentorRepository mentorRepository;
    private final MemberRepository memberRepository;

    public void createMentor(Mentor mentorFeedback, long roomId, long mentorId) {
        Member mentee = extractMemberFromPrincipal(memberRepository);
        Member mentor = memberRepository.findById(mentorId)
                        .orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Optional<Mentor> findMentorFeedback = mentorRepository.findByMenteeAndMentor(mentee,mentor);

        if (findMentorFeedback.isPresent()) {
            Mentor mentorFeedbackEntity = findMentorFeedback.get();
            mentorFeedbackEntity.setRoomId(roomId);
            mentorFeedbackEntity.setStarRating(mentorFeedback.getStarRating());
            mentorFeedbackEntity.setContent(mentorFeedback.getContent());
            mentorRepository.save(mentorFeedbackEntity);
        } else {
            mentorFeedback.setMentor(mentor);
            mentorFeedback.setMentee(mentee);
            mentorFeedback.setRoomId(roomId);
            mentorRepository.save(mentorFeedback);
        }
    }

    public void deleteMentorForAdmin(long mentorFeedbackId){
        Mentor mentor = mentorRepository.findById(mentorFeedbackId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.INTERVIEW_NOT_FOUND));
        mentorRepository.delete(mentor);
    }

    public Page<Mentor> getMentorPage(int page, int size, boolean adminMode) {
        Pageable pageable = PageRequest.of(page, size);
        if(adminMode){
            return mentorRepository.findAll(pageable);
        }else{
            Member mentor = extractMemberFromPrincipal(memberRepository);
            return mentorRepository.findAllMentor(mentor,pageable);
        }
    }

}
