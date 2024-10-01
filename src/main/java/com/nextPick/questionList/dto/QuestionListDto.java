package com.nextPick.questionList.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class QuestionListDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class answerCheck{
        @NotNull
        private String answer;
    }
}
