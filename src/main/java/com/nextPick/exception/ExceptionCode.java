package com.nextPick.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {

    //Solves
    SOLVE_NOT_FOUND(404, "푼 문제를 찾을 수 없습니다."),

    //questionCategory
    QUESTION_CATEGORY_NOT_FOUND(404, "문제의 카테고리를 찾을 수 없습니다."),

    //questionList
    QUESTION_NOT_FOUND(404, "문제를 찾을 수 없습니다."),
    QUESTION_EXISTS(409, "문제가 이미 존재합니다."),

    //member
    MEMBER_NOT_FOUND(404,"Member Not Found"),
    MEMBER_EXISTS(409,"Member exists"),
    EMAIL_EXISTS(409,"Member exists"),
    NICKNAME_EXISTS(409, "NickName exists"),

    //토큰 인증 관련
    UNAUTHORIZED_MEMBER(401, "토큰 인증에 실패했습니다."),
    TOKEN_INVALID(403, "토큰값이 유효하지 않습니다."),

    //Board 관련
    BOARD_NOT_FOUND(404, "게시글을 찾을 수 없습니다."),
    INVALID_BOARD_REQUEST(400, "잘못된 게시글 요청입니다.");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;
}