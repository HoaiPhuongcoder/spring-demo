package com.hoaiphuong.demo.common.response;

import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;

import java.util.List;
import java.util.function.Function;

public record CursorPage<T>(List<T> content, String nextCursor, boolean hasNext, int size) {
    public static <E, T> CursorPage<T> of(Window<E> window,
                                          Function<E, T> mapper,
                                          Function<ScrollPosition, String> encoder){
        List<T> content = window.getContent().stream().map(mapper).toList();
        String next = (window.hasNext() && !window.isEmpty())
                ? encoder.apply(window.positionAt(window.size() - 1))
                : null;
        return new CursorPage<>(content, next, window.hasNext(), content.size());
    }

    public static <E, T> CursorPage<T> of(List<E> rows, int limit,
                                          Function<E, T> mapper,
                                          Function<E, String> cursorOf){
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be >= 1, got: " + limit);
        }
        boolean hasNext = rows.size() > limit;
        List<E> page = hasNext ? rows.subList(0, limit) : rows;

        String next = hasNext ? cursorOf.apply(page.get(page.size() - 1)) : null;
        List<T> content = page.stream().map(mapper).toList();

        return new CursorPage<>(content, next, hasNext, content.size());
    }
}
