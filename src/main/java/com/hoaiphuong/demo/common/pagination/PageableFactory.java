package com.hoaiphuong.demo.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.Map;

public final class PageableFactory {
    private PageableFactory() {}

    public static Pageable create(PageQuery q, Map<String, String> sortMap, String defaultSortKey ){
        int page = q.page() == null ? 0 : q.page();
        int size = q.size() == null ? 0 : q.size();

        String field = (q.sortBy() != null && sortMap.containsKey(q.sortBy()))
                ? sortMap.get(q.sortBy()) :
                sortMap.get(defaultSortKey);
        Sort.Direction direction = "asc".equalsIgnoreCase(q.direction())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, field).and(Sort.by(Sort.Direction.ASC, "id"));

        return PageRequest.of(page, size, sort);
    }
}
