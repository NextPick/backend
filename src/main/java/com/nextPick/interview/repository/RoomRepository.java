package com.nextPick.interview.repository;

import com.nextPick.interview.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nextPick.interview.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByUuid(String uuid);
    Optional<Room> findBySessionId(String sessionId);
    Room findByParticipants(Participant participant);
}
