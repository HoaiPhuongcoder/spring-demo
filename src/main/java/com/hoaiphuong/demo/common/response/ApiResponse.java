package com.hoaiphuong.demo.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hoaiphuong.demo.common.exception.ErrorCode;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        Meta meta,
        List<FieldError> errors,
        Instant timestamp
) {

    public record FieldError(String code, String message) {
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "SUCCESS", "OK",
                data, null, null, Instant.now());
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, "SUCCESS", "OK",
                null, null, null, Instant.now());
    }

    public static <T> ApiResponse<List<T>> ok(Page<T> page) {
        Meta meta = new PageMeta(page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.hasNext());
        return new ApiResponse<>(true, "SUCCESS", "OK", page.getContent(),
                meta, null, Instant.now());
    }

    public static <T> ApiResponse<List<T>> ok(CursorPage<T> page) {
        Meta meta = new CursorMeta(page.size(),page.hasNext(),page.nextCursor());
        return new ApiResponse<>(true, "SUCCESS", "OK", page.content(),
                meta, null, Instant.now());
    }

    public static ApiResponse<Void> error(ErrorCode ec) {
        return new ApiResponse<>(false, ec.name(), ec.getMessage(),
                null, null, null, Instant.now());
    }

    public static ApiResponse<Void> error(ErrorCode ec, String message, List<FieldError> errors) {
        return new ApiResponse<>(false, ec.name(), message, null, null, errors, Instant.now());
    }

}
