package com.sparta.delivery.domain.delivery.dto.delivery;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeliveryRequestDto {
    private UUID orderId;
    private String deliveryAddress;
    private Double latitude;
    private Double longitude;
    private Long receiverId;
    private String receiverSlackId;
    private UUID originHubId;
    private UUID destinationHubId;

}
