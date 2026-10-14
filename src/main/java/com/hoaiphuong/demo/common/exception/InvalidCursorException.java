package com.hoaiphuong.demo.common.exception;

public class InvalidCursorException extends BusinessException {
    public InvalidCursorException() {
        super(ErrorCode.INVALID_CURSOR);
    }
}