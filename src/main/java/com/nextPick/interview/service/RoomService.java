package com.nextPick.interview.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.interview.dto.ParticipantDto;
import com.nextPick.interview.entity.Participant;
import com.nextPick.interview.entity.Room;
import com.nextPick.interview.mapper.ParticipantMapper;
import com.nextPick.interview.repository.ParticipantRepository;
import com.nextPick.interview.repository.RoomRepository;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.member.service.MemberService;
import com.nextPick.utils.ExtractMemberAndVerify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService extends ExtractMemberAndVerify {
    private final RoomRepository roomRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;

    public Room createRoom(Room room) {
        // 토큰 이용하여 member 찾기
       Member member = extractMemberFromPrincipal(memberRepository);
       // 방에 member 저장
       room.setMember(member);
       // 로그로 방 uuid 보여주기
       log.info(room.getUuid());
       // repository 방 저장
       Room savedRoom = roomRepository.save(room);

       // 방 만든 사람 참가자로 넣어주기
       Participant participant = new Participant();
       participant.setMember(member);
       participant.setRoom(room);
       participantRepository.save(participant);

       return savedRoom;
    }

    public Long findRoomsCount() {
        return (long) roomRepository.findAll().size();
    }

    public void deleteRoom(String uuid) {
        Room room = findRoomByUuid(uuid);
        List<Participant> participantList = participantRepository.findAllByRoomUuid(room.getUuid());
        participantRepository.deleteAll(participantList);
        roomRepository.delete(room);
    }

    public Room findRoomByUuid(String uuid) {
        Optional<Room> optionalRoom = roomRepository.findByUuid(uuid);
        Room findRoom = optionalRoom.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ROOM_NOT_FOUND));
        return findRoom;
    }
}
