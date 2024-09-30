package com.nextPick.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ParticipantDto {
    @Getter
    @AllArgsConstructor
    public static class Post {
        private Long memberId;
        @Setter
        private String uuid;
    }
}
