package com.nextPick.boardComment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class BoardCommentDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private Long memberId;  // memberId 추가
        @NotBlank
        private String content;
        private Long parentCommentId;  // 대댓글인 경우 부모 댓글 ID
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Patch {
        private Long memberId;
        @NotBlank
        private String content;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private long boardCommentId;
        private long memberId;
        private long boardId;
        private Long parentCommentId;
        private String content;
        private String nickname;
    }
}
