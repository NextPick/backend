package com.nextPick.interview.repository;

import com.nextPick.interview.entity.Participant;
import com.nextPick.interview.entity.Room;
import com.nextPick.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByRoomAndMember(Room room, Member member);
    List<Participant> findAllByRoomUuid(String roomUuid);
    Optional<Participant> findByRoom(Room room);
}
