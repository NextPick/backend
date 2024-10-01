package com.nextPick.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class RoomDto {
    @Getter
    @AllArgsConstructor
    public static class Post {
        private String title;
        private String email;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long room_count;
    }
}
