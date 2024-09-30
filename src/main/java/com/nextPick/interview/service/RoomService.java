package com.nextPick.interview.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.interview.entity.Room;
import com.nextPick.interview.repository.RoomRepository;
import com.nextPick.member.entity.Member;
import com.nextPick.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final MemberService memberService;

    public Room createRoom(Room room) {
       Member member = memberService.findMember(room.getMember());
       room.setMember(member);
       log.info(room.getUuid());
       return roomRepository.save(room);
    }

    public Long findRoomsCount() {
        return (long) roomRepository.findAll().size();
    }

    public void deleteRoom(String uuid) {
        Room room = findRoomByUuid(uuid);
        roomRepository.delete(room);
    }

    public Room findRoomByUuid(String uuid) {
        Optional<Room> optionalRoom = roomRepository.findByUuid(uuid);
        Room findRoom = optionalRoom.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ROOM_NOT_FOUND));
        return findRoom;
    }
}
