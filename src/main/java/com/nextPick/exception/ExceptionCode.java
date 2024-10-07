package com.nextPick.exception;

import com.nextPick.interview.entity.Participant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {

    //member
    MEMBER_NOT_FOUND(404,"Member Not Found"),
    MEMBER_EXISTS(409,"Member exists"),
    EMAIL_EXISTS(409,"Member exists"),
    NICKNAME_EXISTS(409, "NickName exists"),

    //토큰 인증 관련
    UNAUTHORIZED_MEMBER(401, "토큰 인증에 실패했습니다."),
    TOKEN_INVALID(403, "토큰값이 유효하지 않습니다."),

    // room
    ROOM_NOT_FOUND(404, "Room Not Found"),

    // participant
    PARTICIPANT_NOT_FOUND(404, "Participant Not Found"),
    PARTICIPANT_FULL(404, "Participant Full");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;
}