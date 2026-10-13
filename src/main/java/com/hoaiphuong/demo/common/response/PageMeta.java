package com.hoaiphuong.demo.common.response;

public record PageMeta(int page, int size, long totalElements, int totalPages, boolean hasNext) implements Meta {
}
