package com.hoaiphuong.demo.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pagination info: PageMeta (offset) or CursorMeta (cursor)")
public sealed interface  Meta permits PageMeta, CursorMeta {}
