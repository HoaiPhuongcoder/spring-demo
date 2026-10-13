package com.hoaiphuong.demo.common.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CursorQuery(
        @Min(1)
        @Max(100)
        Integer limit,
        String cursor) {}
