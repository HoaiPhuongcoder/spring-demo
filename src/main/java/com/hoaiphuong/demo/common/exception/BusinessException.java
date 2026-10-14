package com.hoaiphuong.demo.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    // true when the message was passed explicitly: it is returned as-is instead of the localized ErrorCode message
    private final boolean customMessage;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.customMessage = false;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.customMessage = true;
    }
}
