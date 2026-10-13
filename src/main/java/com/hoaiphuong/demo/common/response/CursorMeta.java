package com.hoaiphuong.demo.common.response;

public record CursorMeta(int size, boolean hasNext, String nextCursor) implements Meta {
}
