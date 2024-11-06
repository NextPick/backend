package com.nextPick.board.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("Q")
@Getter
@Setter
public class QuestionBoard extends Board {

    @Column(name = "board_category")
    private QuestionBoard.BoardCategory boardCategory = BoardCategory.FE;
    @AllArgsConstructor
    @NoArgsConstructor
    @NotNull
    public enum BoardCategory {
        BE("백엔드"),
        FE("프론트엔드"),
        CS("컴퓨터 사이언스");

        @Getter
        @Setter
        private String boardCategory;
    }
}
