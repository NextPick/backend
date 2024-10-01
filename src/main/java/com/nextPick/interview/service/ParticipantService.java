package com.nextPick.interview.service;

import com.nextPick.interview.entity.Participant;
import com.nextPick.interview.repository.ParticipantRepository;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.member.service.MemberService;
import com.nextPick.utils.ExtractMemberAndVerify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantService extends ExtractMemberAndVerify {
    private final ParticipantRepository participantRepository;
    private final RoomService roomService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public Participant createParticipant(Participant participant) {
        Member member = extractMemberFromPrincipal(
                SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), memberRepository);
        participant.setMember(member);
        participant.setRoom(roomService.findRoomByUuid(participant.getRoom().getUuid()));

        return participantRepository.save(participant);
    }

    public void deleteParticipant(Participant participant) {
        Participant findParticipant = participantRepository.findByRoomUuidAndMemberMemberId(
                participant.getRoom().getUuid(), participant.getMember().getMemberId());
        participantRepository.delete(findParticipant);
    }
}
