package com.sparta.order.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

public interface ProductDto {
    @Data
    @Builder
    class Response {
        private final UUID productId;
        private final String productName;
        private final String description;
        private final Integer stock;
        private final UUID hubId;
        private final UUID companyId;
    }

    @Data
    @Builder
    class Modify {
        private final String productName;
        private final String description;
        private final Integer stock;
        private final UUID hubId;
        private final UUID companyId;
    }
}
