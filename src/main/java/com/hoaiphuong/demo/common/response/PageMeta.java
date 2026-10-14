package com.hoaiphuong.demo.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Offset pagination info")
public record PageMeta(
        @Schema(description = "Current page, zero-based", example = "0") int page,
        @Schema(description = "Page size (as requested)", example = "20") int size,
        @Schema(description = "Total number of elements", example = "135") long totalElements,
        @Schema(description = "Total number of pages", example = "7") int totalPages,
        @Schema(description = "Whether there is a next page", example = "true") boolean hasNext
) implements Meta {
}
