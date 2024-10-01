package com.nextPick.interview.dto;

import com.nextPick.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ParticipantDto {
    @Getter
    public static class Post {
        @Setter
        private String uuid;
    }

    @Getter
    @Setter
    public static class Response {
        private String nickname;
        private Member.memberOccupation occupation;
    }
}
