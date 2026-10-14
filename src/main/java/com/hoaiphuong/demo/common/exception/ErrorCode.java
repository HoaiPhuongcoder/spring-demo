package com.hoaiphuong.demo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


/**
 * Error codes returned to clients. Messages are localized: the text for each code lives in
 * {@code i18n/messages*.properties} under the key {@link #getMessageKey()}.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    MALFORMED_REQUEST(HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_CURSOR(HttpStatus.BAD_REQUEST),

    // Authentication, authorization
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    FORBIDDEN(HttpStatus.FORBIDDEN),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(HttpStatus.CONFLICT);



    private final HttpStatus status;

    public String getMessageKey() {
        return "error." + name();
    }
}
