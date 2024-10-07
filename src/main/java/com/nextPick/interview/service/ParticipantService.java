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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantService extends ExtractMemberAndVerify {
    private final ParticipantRepository participantRepository;
    private final RoomService roomService;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Participant createParticipant(String uuid) {
        if (findParticipantCount(uuid) == 4) {
            throw new BusinessLogicException(ExceptionCode.PARTICIPANT_FULL);
        }
        Participant participant = new Participant();
        participant.setMember(extractMemberFromPrincipal(memberRepository));
        participant.setRoom(roomService.findRoomByUuid(uuid));

        return participantRepository.save(participant);
    }

    @Transactional
    public List<Participant> findParticipants(String uuid) {
        List<Participant> participants = participantRepository.findAllByRoomUuid(uuid);
        if (participants.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.PARTICIPANT_NOT_FOUND);
        }
        return participants;
    }

    @Transactional
    public void deleteParticipant(String uuid) {
        Member member = extractMemberFromPrincipal(memberRepository);
        Room room = roomService.findRoomByUuid(uuid);
        participantRepository.findByRoomAndMember(
                room, member).ifPresent(participantRepository::delete);
        if (participantRepository.findByRoom(room).isEmpty()) {
            roomRepository.delete(room);
        }
    }

    @Transactional
    public int findParticipantCount(String uuid) {
        return participantRepository.findAllByRoomUuid(uuid).size();
    }
}
