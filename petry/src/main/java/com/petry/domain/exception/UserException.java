package com.petry.domain.exception;

import com.petry.global.exception.BaseException;
import com.petry.global.exception.BaseExceptionType;

public class UserException extends BaseException {
    private BaseExceptionType exceptionType;

    public UserException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
