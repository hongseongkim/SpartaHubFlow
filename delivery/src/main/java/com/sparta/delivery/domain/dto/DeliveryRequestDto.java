package com.sparta.delivery.domain.dto;

import java.util.UUID;
import lombok.Getter;

@Getter
public class DeliveryRequestDto {
    private UUID orderId;
    private UUID departureHubId;
    private UUID destinationHubId;
    private String deliveryAddress;
    private UUID receiverId;

    public DeliveryDto toDTO() {
        return DeliveryDto.create(orderId, departureHubId, destinationHubId, deliveryAddress, receiverId);
    }
}
