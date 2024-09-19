package com.sparta.delivery.domain.delivery.dto.delivery;

import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class DeliveryResponseDto {

    private Delivery delivery;
    private DeliveryRoute deliveryRoute;

    public static DeliveryResponseDto from(Delivery delivery, DeliveryRoute route) {
        return DeliveryResponseDto.builder()
                .delivery(delivery)
                .deliveryRoute(route)
                .build();
    }
}
