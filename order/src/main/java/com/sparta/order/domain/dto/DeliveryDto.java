package com.sparta.order.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

public interface DeliveryDto {
    @Data
    @Builder
    class Create {

        private final UUID orderId;
        private final UUID departureHubId;
        private final UUID destinationHubId;
        private final String deliveryAddress;
        private final Long receiverId;

    }
}
