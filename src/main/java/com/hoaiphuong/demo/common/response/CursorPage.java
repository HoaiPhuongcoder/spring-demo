package com.hoaiphuong.demo.common.response;

import org.springframework.data.domain.Window;

import java.util.List;

public record CursorPage<T>(List<T> content, String nextCursor, boolean hasNext, int size) {

}
