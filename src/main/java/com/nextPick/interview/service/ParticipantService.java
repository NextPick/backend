package com.nextPick.interview.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.interview.entity.Participant;
import com.nextPick.interview.entity.Room;
import com.nextPick.interview.repository.ParticipantRepository;
import com.nextPick.interview.repository.RoomRepository;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.member.service.MemberService;
import com.nextPick.utils.ExtractMemberAndVerify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantService extends ExtractMemberAndVerify {
    private final ParticipantRepository participantRepository;
    private final RoomService roomService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;

    public Participant createParticipant(String uuid) {
        Participant participant = new Participant();
        Member member = extractMemberFromPrincipal(memberRepository);
        participant.setMember(member);
        participant.setRoom(roomService.findRoomByUuid(uuid));

        return participantRepository.save(participant);
    }

    public List<Participant> findParticipants(String uuid) {
        List<Participant> participants = participantRepository.findAllByRoomUuid(uuid);
        if (participants.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.PARTICIPANT_NOT_FOUND);
        }
        return participants;
    }

    public void deleteParticipant(String uuid) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Room room = roomService.findRoomByUuid(uuid);
        participantRepository.findByRoomAndMember(
                room, member).ifPresent(participant -> participantRepository.delete(participant));
        if (participantRepository.findByRoom(room).isEmpty()) {
            roomRepository.delete(room);
        }
    }
}
