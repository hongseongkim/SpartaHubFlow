package com.sparta.delivery.domain.delivery.dto.person;

import com.sparta.delivery.domain.delivery.model.enums.DeliveryPersonType;
import java.util.UUID;
import lombok.Getter;

@Getter
public class DeliveryPersonRequestDto {
    private Long userId;
    private String deliveryPersonSlackId;
    private UUID hubId;
    private DeliveryPersonType deliveryPersonType;
}
