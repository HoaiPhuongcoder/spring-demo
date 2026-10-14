package com.hoaiphuong.demo.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hoaiphuong.demo.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

@Schema(description = "Common envelope for every response. Null fields are omitted from the JSON")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @Schema(description = "Whether the request succeeded", example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED)
        boolean success,

        @Schema(description = "`SUCCESS` on success, otherwise an error code (ErrorCode)", example = "SUCCESS",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String code,

        @Schema(description = "Message, localized by the Accept-Language header", example = "OK",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String message,

        @Schema(description = "Response payload, absent on errors")
        T data,

        @Schema(description = "Pagination info, only present on paginated list endpoints",
                oneOf = {PageMeta.class, CursorMeta.class})
        Meta meta,

        @Schema(description = "Per-field error details, only present when request validation fails")
        List<FieldError> errors,

        @Schema(description = "Response time (UTC)", example = "2026-01-01T00:00:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Instant timestamp
) {

    @Schema(description = "Error of a single field / parameter")
    public record FieldError(
            @Schema(description = "Name of the invalid field / parameter", example = "email") String code,
            @Schema(description = "Error description", example = "must be a well-formed email address") String message) {
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

    public static ApiResponse<Void> error(ErrorCode ec, String message, List<FieldError> errors) {
        return new ApiResponse<>(false, ec.name(), message, null, null, errors, Instant.now());
    }

}
