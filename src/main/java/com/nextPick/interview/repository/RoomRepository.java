package com.nextPick.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextPick.interview.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    
}
