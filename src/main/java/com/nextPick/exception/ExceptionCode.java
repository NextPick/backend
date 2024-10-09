package com.nextPick.exception;

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
    MEMBER_BANNED(403,"이용 정지 당한 사용자입니다."),
    MEMBER_EXISTS(409,"Member exists"),
    EMAIL_EXISTS(409,"Member exists"),
    NICKNAME_EXISTS(409, "NickName exists"),

    //토큰 인증 관련
    UNAUTHORIZED_MEMBER(401, "토큰 인증에 실패했습니다."),
    TOKEN_INVALID(403, "토큰값이 유효하지 않습니다.");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;
}