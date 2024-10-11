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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RoomService extends ExtractMemberAndVerify {
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantService participantService;

    public RoomService(RoomRepository roomRepository, MemberRepository memberRepository, ParticipantRepository participantRepository, ParticipantService participantService) {
        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;
        this.participantRepository = participantRepository;
        this.participantService = participantService;
    }

    @Transactional
    public Room createRoom(Room room) {
        // 토큰 이용하여 member 찾기
       Member member = extractMemberFromPrincipal(memberRepository);

       // 멘티일 경우 방 생성 막기
       if (member.getType() == Member.memberType.MENTEE) {
           throw new BusinessLogicException(ExceptionCode.ROOM_CANT_MAKE);
       }
       // 방에 member 저장
       room.setMember(member);
       // 로그로 방 uuid 보여주기
       log.info(room.getUuid());
       // repository 방 저장
       Room savedRoom = roomRepository.save(room);

       // 방 만든 사람 참가자에 넣기
       Participant participant = new Participant();
       participant.setMember(member);
       participant.setRoom(room);
       participantRepository.save(participant);

       return savedRoom;
    }

    @Transactional
    public int findRoomsCount() {
        // 전체 방 찾기
        List<Room> rooms = roomRepository.findAll();
        // 들어갈 수 있는 방
        int canEnterRoomCount = 0;
        // 방 하나씩 참가자 4명이하 방 개수 찾기
        for (Room room : rooms) {
            int participantCount = participantService.findParticipantCount(room.getUuid());
            if (participantCount < 4) {
                canEnterRoomCount++;
            }
        }

        return canEnterRoomCount;
    }

    @Transactional
    public Room findActiveRoom(String occupation) {
        List<Room> rooms = roomRepository.findAll();

        for (Room room : rooms) {
            // 방의 인원수가 4명 이하이고 룸 직군이 받은 것과 같을 때 return
            System.out.println(room.getParticipants().size());
            if (room.getParticipants().size() < 4 && room.getOccupation().toString().equals(occupation)) {
                return room;
            }
        }

        throw new BusinessLogicException(ExceptionCode.ROOM_NOT_ACTIVE);
    }

    @Transactional
    public void deleteRoom(String uuid) {
        Room room = findRoomByUuid(uuid);
        List<Participant> participantList = participantRepository.findAllByRoomUuid(room.getUuid());
        participantRepository.deleteAll(participantList);
        roomRepository.delete(room);
    }

    @Transactional(readOnly = true)
    public Room findRoomByUuid(String uuid) {
        Optional<Room> optionalRoom = roomRepository.findByUuid(uuid);
        return optionalRoom.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ROOM_NOT_FOUND));
    }
}
