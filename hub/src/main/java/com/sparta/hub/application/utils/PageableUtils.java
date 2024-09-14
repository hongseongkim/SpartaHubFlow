package com.sparta.hub.application.utils;

import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final List<Integer> ALLOWED_PAGE_SIZES = Arrays.asList(10, 30, 50);

    // 페이지 건수 제한 로직
    public static Pageable applyPageSizeLimit(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        if (!ALLOWED_PAGE_SIZES.contains(pageSize)) {
            return PageRequest.of(pageable.getPageNumber(), DEFAULT_PAGE_SIZE, pageable.getSort());
        }
        return pageable;
    }

    // 정렬 조건 검증 및 기본값 설정 로직
    public static Pageable applyDefaultSortIfNecessary(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort.get().noneMatch(order -> order.getProperty().equals("createdAt") || order.getProperty().equals("updatedAt"))) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.asc("createdAt")));
        }
        return pageable;
    }
}
