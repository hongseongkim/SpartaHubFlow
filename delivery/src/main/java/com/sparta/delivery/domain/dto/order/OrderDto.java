package com.sparta.delivery.domain.dto.order;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

public interface OrderDto {

    @Data
    @Builder
    class Response {
        private final UUID orderId;
        private final UUID productId;
        private final Integer quantity;
        private final OrderStatusEnum orderStatus;

    }

}
