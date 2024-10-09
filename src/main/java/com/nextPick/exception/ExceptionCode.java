package com.nextPick.exception;

import com.nextPick.interview.entity.Participant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {

    //Feedbacks
    INTERVIEW_NOT_FOUND(404, "면접 피드백을 찾을 수 없습니다."),
    MENTOR_FEEDBACK_NOT_FOUND(404, "멘토 피드백을 찾을 수 없습니다."),
    MENTOR_FEEDBACK_EXISTS(409, "멘토 피드백이 이미 존재합니다."),

    //Reports
    REPORTS_NOT_FOUND(404, "신고 내역을 찾을 수 없습니다."),
    REPORTS_EXISTS(409, "신고 내역이 이미 존재합니다."),

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

    // room
    ROOM_NOT_FOUND(404, "Room Not Found"),
    ROOM_CANT_MAKE(401, "멘티는 방을 생성할 수 없습니다."),

    // participant
    PARTICIPANT_NOT_FOUND(404, "Participant Not Found"),
    PARTICIPANT_FULL(404, "Participant Full");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;
}