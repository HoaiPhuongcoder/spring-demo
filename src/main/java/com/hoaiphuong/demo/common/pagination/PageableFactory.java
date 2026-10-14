package com.hoaiphuong.demo.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.Map;
import java.util.Objects;

public final class PageableFactory {
    public static final int DEFAULT_SIZE = 20;

    private PageableFactory() {}

    public static Pageable create(PageQuery q, Map<String, String> sortMap, String defaultSortKey ){
        int page = q.page() == null ? 0 : q.page();
        int size = q.size() == null ? DEFAULT_SIZE : q.size();

        String defaultField = Objects.requireNonNull(sortMap.get(defaultSortKey),
                "defaultSortKey '" + defaultSortKey + "' is not in sortMap");
        String field = (q.sortBy() != null && sortMap.containsKey(q.sortBy()))
                ? sortMap.get(q.sortBy())
                : defaultField;
        Sort.Direction direction = "asc".equalsIgnoreCase(q.direction())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, field);
        // Add id as a tie-breaker so the order stays stable across pages
        if (!"id".equals(field)) {
            sort = sort.and(Sort.by(Sort.Direction.ASC, "id"));
        }

        return PageRequest.of(page, size, sort);
    }
}
