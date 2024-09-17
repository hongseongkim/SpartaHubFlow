package com.sparta.delivery.domain.delivery.dto.route;

import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class DeliveryRouteDto {

    private DeliveryRoute deliveryRoute;

    public static DeliveryRouteDto from(DeliveryRoute route) {
        return DeliveryRouteDto.builder()
                .deliveryRoute(route)
                .build();
    }
}
