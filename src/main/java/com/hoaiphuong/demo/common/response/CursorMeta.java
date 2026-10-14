package com.hoaiphuong.demo.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cursor pagination info")
public record CursorMeta(
        @Schema(description = "Number of elements returned in this batch", example = "20") int size,
        @Schema(description = "Whether more data is available", example = "true") boolean hasNext,
        @Schema(description = "Cursor for the next batch, null when there is no more data",
                example = "eyJpZCI6IjhiZTRkZjYxLTkzY2EtMTFkMi1hYTBkLTAwZTA5ODAzMmI4YyJ9",
                nullable = true) String nextCursor
) implements Meta {
}
