package com.hoaiphuong.demo.common.pagination;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PageQuery(
        @Parameter(description = "Page number, zero-based",
                schema = @Schema(type = "integer", minimum = "0", defaultValue = "0"))
        @Min(0) Integer page,

        @Parameter(description = "Number of elements per page",
                schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "20"))
        @Min(1) @Max(100) Integer size,

        @Parameter(description = "Field to sort by. See each endpoint's description for allowed values; "
                + "unknown values fall back to the default field")
        String sortBy,

        @Parameter(description = "Sort direction; anything other than `asc` is treated as `desc`",
                schema = @Schema(type = "string", allowableValues = {"asc", "desc"}, defaultValue = "desc"))
        String direction) {}
