package com.nextPick.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class RoomDto {
    @Getter
    public static class Post {
        private String title;
    }

    @Getter
    @Setter
    public static class Response {
        private Long room_count;
    }
}
