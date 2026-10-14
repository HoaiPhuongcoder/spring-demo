package com.hoaiphuong.demo.common.exception;

import com.hoaiphuong.demo.common.i18n.Messages;
import com.hoaiphuong.demo.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Messages messages;

    private ResponseEntity<ApiResponse<Void>> build(ErrorCode ec, String message, List<ApiResponse.FieldError> errors) {
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.error(ec, message, errors));
    }

    private ResponseEntity<ApiResponse<Void>> build(ErrorCode ec, List<ApiResponse.FieldError> errors) {
        return build(ec, messages.get(ec), errors);
    }

    // Business errors thrown on purpose by our code
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        String message = ex.isCustomMessage() ? ex.getMessage() : messages.get(ex.getErrorCode());
        return build(ex.getErrorCode(), message, null);
    }

    // @Valid on @RequestBody / @ModelAttribute
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleBodyValidation(MethodArgumentNotValidException ex) {
        List<ApiResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ApiResponse.FieldError(f.getField(), messages.get(f)))
                .toList();
        return build(ErrorCode.VALIDATION_ERROR, errors);
    }

    // @Validated on @RequestParam / @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException ex) {
        List<ApiResponse.FieldError> errors = ex.getConstraintViolations().stream()
                .map(v -> new ApiResponse.FieldError(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        return build(ErrorCode.VALIDATION_ERROR, errors);
    }

    // Invalid JSON syntax or wrong data types in the body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(ErrorCode.MALFORMED_REQUEST, null);
    }

    // Missing required parameter
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(ErrorCode.VALIDATION_ERROR, messages.get("error.param.missing", ex.getParameterName()), null);
    }

    // Wrong parameter type, e.g. ?status=ABC that is not an enum value
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(ErrorCode.VALIDATION_ERROR, messages.get("error.param.invalid", ex.getName()), null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return build(ErrorCode.METHOD_NOT_ALLOWED, null);
    }

    // Unknown path (Spring 6.1+)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException ex) {
        return build(ErrorCode.RESOURCE_NOT_FOUND, null);
    }

    // Important: @PreAuthorize throws this from inside the controller;
    // without a dedicated handler it would fall through to the generic handler and become a 500
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return build(ErrorCode.FORBIDDEN, null);
    }

    // Constraint violation (duplicate unique key...) that the service did not check beforehand
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation", ex);
        return build(ErrorCode.VALIDATION_ERROR, messages.get("error.data-integrity"), null);
    }

    // Everything else: log the full error, do NOT leak details to the client
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(ErrorCode.INTERNAL_ERROR, null);
    }
}
