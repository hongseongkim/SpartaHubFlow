package com.sparta.delivery.domain.delivery.dto;

import java.util.UUID;
import lombok.Getter;

@Getter
public class DeliveryRequestDto {
    private UUID orderId;
    private String deliveryAddress;
    private Long receiverId;
    private String receiverSlackId;
    private UUID originHubId;
    private UUID destinationHubId;

}
