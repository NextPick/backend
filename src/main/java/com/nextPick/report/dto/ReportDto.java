package com.nextPick.report.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ReportDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotNull
        private String nickname;

        @NotNull
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response{
        private long reporterId;
        private long respondentId;
        private String content;
        private LocalDateTime createAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Responses{
        private long reportId;
        private long reporterId;
        private long respondentId;
        private String content;
        private LocalDateTime createAt;
    }
}
