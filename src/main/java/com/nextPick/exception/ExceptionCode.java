package com.nextPick.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {

    //statistics
    STATISTICS_NOT_FOUND(404,"해당 통계 이름과 일치하는 정보를 찾을 수 없습니다."),

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
    TOKEN_INVALID(403, "토큰값이 유효하지 않습니다."),
    INVALID_BOARD_TYPE (403 , "잘못된 게시판 입니다"),
    //Board 관련
    BOARD_NOT_FOUND(404, "게시글을 찾을 수 없습니다."),
    INVALID_BOARD_REQUEST(400, "잘못된 게시글 요청입니다."),
    BOARD_DELETED(404,"게시물이 삭제된 상태입니다."),
    //Comment 관련
    COMMENT_NOT_FOUND(404, "댓글을 찾을 수 없습니다."),
    UNAUTHORIZED_ACTION(403, "권한이 없습니다."),

    //s3 관련
    IMAGE_UPLOAD_FAILED(403, "이미지를 올릴 수 없습니다"),
    IMAGE_TOO_LARGE(405, "이미지가 너무 큽니다"),
    INVALID_IMAGE_FORMAT(403, "이미지를 포맷할 수 없습니다"),
    IMAGE_DELETE_FAILED(403, "이미지를 삭제할 수 없습니다"),
    S3_ACCESS_DENIED(403 ,"S3를 ACCESS 할 수 없습니다 "),

    S3_BUCKET_NOT_FOUND(404, "버킷을 찾을 수 없습니다"),
    S3_CONNECTION_ERROR(403, "연결을 할 수 없습니다"),

    //mail 관련
    UNABLE_TO_SEND_EMAIL(400, "이용불가한 이메일입니다."),
    INVALID_REPORT_TARGET(400, "Report target is invalid");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;
}