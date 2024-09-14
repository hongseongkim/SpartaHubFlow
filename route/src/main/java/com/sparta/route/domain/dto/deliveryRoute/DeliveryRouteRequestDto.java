package com.sparta.route.domain.dto.deliveryRoute;

import com.sparta.route.domain.model.deliveryRoute.enums.DeliveryStatus;
import java.util.UUID;
import lombok.Getter;

@Getter
public class DeliveryRouteRequestDto {

    private UUID deliveryId;
    private UUID originHubId;
    private UUID destinationHubId;
    private Long deliveryPersonId;
    private Integer sequence;
    private Double estimatedDistance;
    private Integer estimatedTime;
    private DeliveryStatus currentStatus;

    public DeliveryRouteDto toDTO() {
        return DeliveryRouteDto.create(deliveryId, originHubId, destinationHubId, sequence, estimatedDistance, estimatedTime, currentStatus);
    }
}
