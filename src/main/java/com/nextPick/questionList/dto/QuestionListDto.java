package com.nextPick.questionList.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nextPick.questionCategory.entity.QuestionCategory;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class QuestionListDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post{
        @NotNull
        private String answer;

        @NotNull
        private String question;

        @NotNull
        private long questionCategoryId;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Patch{

        @NotNull
        private String answer;

        @NotNull
        private String question;

        @NotNull
        private int correctCount;

        @NotNull
        private int wrongCount;

        @NotNull
        private long QuestionCategoryId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class answerCheck{
        @NotNull
        private String answer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private long questionListId;
        private String question;
        private String answer;
        private int correctCount;
        private int wrongCount;
        private int correctRate;
        private QuestionCategory questionCategory;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponsesTypeNone {
        private long questionListId;
        private String question;
        private int correctRate;
        private QuestionCategory questionCategory;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponsesTypeRandom {
        private long questionListId;
        private String question;
        private String answer;
        private int correctRate;
        private QuestionCategory questionCategory;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResponseSolvesId{
        private Long solvesId;
        private boolean result;
    }
}
