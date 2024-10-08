package com.nextPick.solves.dto;

import com.nextPick.questionCategory.entity.QuestionCategory;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

public class SolvesDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private long solvesId;
        private String question;
        private String answer;
        private String myAnswer;
        private String modifiedAt;
        private boolean correct;
        private int correctRate;
        private long questionCategoryId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Responses {
        private long solvesId;
        private String question;
        private boolean correct;
        private String createdAt;
        private String modifiedAt;
        private int correctRate;
        private long questionCategoryId;
    }

}
