package com.petry.domain.exception;

import com.petry.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum DiaryExceptionType implements BaseExceptionType {
    DIARY_NOT_FOUNT(700, HttpStatus.NOT_FOUND, "존재하지 않은 다이어리입니다."),
    NOT_AUTHORITY_UPDATE(701, HttpStatus.FORBIDDEN, "다이어리를 수정할 권한이 없습니다."),
    NOT_AUTHORITY_DELETE(702, HttpStatus.FORBIDDEN, "다이어리를 삭제할 권한이 없습니다.");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

    DiaryExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
