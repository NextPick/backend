package com.nextPick.questionCategory.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

public class QuestionCategoryDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private long questionCategoryId;
        private String categoryName;
    }
}
