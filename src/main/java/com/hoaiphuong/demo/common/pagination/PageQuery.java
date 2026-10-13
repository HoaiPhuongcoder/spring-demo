package com.hoaiphuong.demo.common.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PageQuery(
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size,
        String sortBy,
        String direction) {}
