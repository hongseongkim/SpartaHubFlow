package com.sparta.route.domain.dto.deliveryRoute;

import com.sparta.route.domain.model.deliveryRoute.DeliveryRoute;
import com.sparta.route.domain.model.deliveryRoute.enums.DeliveryStatus;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class DeliveryRouteDto {

    private UUID deliveryId;
    private UUID originHubId;
    private UUID destinationHubId;
    private Integer sequence;
    private Double estimatedDistance;
    private Integer estimatedTime;
    private DeliveryStatus currentStatus;

    public static DeliveryRouteDto from(DeliveryRoute route) {
        return DeliveryRouteDto.builder()
                .originHubId(route.getOriginHubId())
                .destinationHubId(route.getDestinationHubId())
                .sequence(route.getSequence())
                .estimatedDistance(route.getEstimatedDistance())
                .estimatedTime(route.getEstimatedTime())
                .currentStatus(route.getCurrentStatus())
                .build();
    }

    public static DeliveryRoute toEntity(DeliveryRouteDto dto) {
        return DeliveryRoute.create(dto.getDeliveryId(), dto.getOriginHubId(), dto.getDestinationHubId());
    }
}
