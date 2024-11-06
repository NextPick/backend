package com.nextPick.interview.dto;

import com.nextPick.interview.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class RoomDto {
    @Getter
    public static class Post {
        private String title;
        private Room.roomOccupation occupation;
    }

    @Getter
    @Setter
    public static class Response {
        private int room_count;
    }

    @Getter
    @Setter
    public static class PostResponse {
        private String title;
        private Long memberId;
        private Long roomId;
        private Room.roomOccupation roomOccupation;
        private String roomUuid;
    }
}
