package com.nextPick.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {

    //member
    MEMBER_NOT_FOUND(404,"Member Not Found");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;
}