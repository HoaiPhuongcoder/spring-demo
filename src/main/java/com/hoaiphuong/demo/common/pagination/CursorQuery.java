package com.hoaiphuong.demo.common.pagination;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CursorQuery(
        @Parameter(description = "Maximum number of elements to return",
                schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "20"))
        @Min(1)
        @Max(100)
        Integer limit,

        @Parameter(description = "Cursor taken from `meta.nextCursor` of the previous response. Omit to get the first batch")
        String cursor) {

    public static final int DEFAULT_LIMIT = 20;

    public int limitOrDefault() {
        return limit == null ? DEFAULT_LIMIT : limit;
    }
}
