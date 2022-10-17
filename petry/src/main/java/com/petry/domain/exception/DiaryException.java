package com.petry.domain.exception;

import com.petry.global.exception.BaseException;
import com.petry.global.exception.BaseExceptionType;

public class DiaryException extends BaseException {
    private BaseExceptionType baseExceptionType;

    public DiaryException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return this.baseExceptionType;
    }
}
