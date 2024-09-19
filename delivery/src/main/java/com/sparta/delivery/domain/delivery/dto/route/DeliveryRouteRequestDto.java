package com.sparta.delivery.domain.delivery.dto.route;

import lombok.Getter;

@Getter
public class DeliveryRouteRequestDto {
    private Integer estimatedTime;
    private Double estimatedDistance;
}
