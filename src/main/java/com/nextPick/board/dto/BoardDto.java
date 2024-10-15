package com.nextPick.board.dto;

import com.nextPick.board.entity.QuestionBoard;
import com.nextPick.board.entity.ReviewBoard;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class BoardDto {


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s]+$", message = "제목은 영문자, 숫자, 공백, 한글만 허용됩니다.")
        private String title;

        @NotBlank
        @Size(min = 1, max = 5000, message = "내용은 1자에서 5000자 이내로 작성 가능합니다.")
        private String content;

        private String contentImg;

        private QuestionBoard.BoardCategory boardCategory;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch {
        private long boardId;

        @NotBlank
        private String title;

        @NotBlank
        @Size(min = 1, max = 5000, message = "내용은 1자에서 5000자 이내로 작성 가능합니다.")
        private String content;

        private String contentImg;

        private LocalDateTime modifiedAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private long boardId;
        private String title;
        private String author;
        private String content;
        private List<String> imageUrls;
        private String dtype;
        private String boardStatus;
        private int likesCount;
        private int viewCount;
        private int commentCount;
        private QuestionBoard.BoardCategory boardCategory;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseBoard {
        private long boardId;
        private String title;
        private String author;
        private String content;
        private List<String> imageUrls;
        //        private List<CommentDto.Response> comments;
        private String dtype;
        private String boardStatus;
        private int likesCount;
        private int viewCount;
        private int commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}
