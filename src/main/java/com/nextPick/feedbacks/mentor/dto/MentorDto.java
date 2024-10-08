package com.nextPick.feedbacks.mentor.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class MentorDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotNull
        private String content;

        @NotNull
        @Min(0)
        @Max(5)
        private int starRating;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Responses{
        private String content;
        private String createdAt;
        private Integer startRating;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponsesForAdmin{
        private String name;
        private String content;
        private String createdAt;
        private Integer startRating;
    }
}
