package com.nextPick.interview.service;

import com.nextPick.interview.entity.Participant;
import com.nextPick.interview.repository.ParticipantRepository;
import com.nextPick.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final RoomService roomService;
    private final MemberService memberService;

    public Participant createParticipant(Participant participant) {
        participant.setMember(memberService
                .findMemberById(participant.getMember().getMemberId()));
        participant.setRoom(roomService.findRoomByUuid(participant.getRoom().getUuid()));

        return participantRepository.save(participant);
    }

    public void deleteParticipant(Participant participant) {
        Participant findParticipant = participantRepository.findByRoomUuidAndMemberMemberId(
                participant.getRoom().getUuid(), participant.getMember().getMemberId());
        participantRepository.delete(findParticipant);
    }
}
