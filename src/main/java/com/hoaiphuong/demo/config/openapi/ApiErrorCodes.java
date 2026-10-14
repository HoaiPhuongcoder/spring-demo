package com.hoaiphuong.demo.config.openapi;

import com.hoaiphuong.demo.common.exception.ErrorCode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the error codes an endpoint can return.
 * OpenApiConfig generates the error responses (grouped by HTTP status) with one example per code.
 * INTERNAL_ERROR (500) is always added to every endpoint and does not need to be declared.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiErrorCodes {
    ErrorCode[] value();
}
