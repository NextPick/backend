package com.nextPick.interview.dto;

import com.nextPick.interview.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class RoomDto {
    @Getter
    public static class Post {
        private String title;
        private Room.roomOccupation Occupation;
    }

    @Getter
    @Setter
    public static class Response {
        private int room_count;
    }
}
