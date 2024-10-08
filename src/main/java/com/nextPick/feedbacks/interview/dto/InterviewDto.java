package com.nextPick.feedbacks.interview.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class InterviewDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotNull
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Responses{
        private String mentor;
        private String content;
        private String createdAt;
    }
}
