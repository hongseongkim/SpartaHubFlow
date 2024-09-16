package com.sparta.delivery.domain.delivery.dto.route;

import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import jakarta.persistence.Column;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class DeliveryRouteDto {

    private UUID deliveryRouteId;
    private UUID originHubId;
    private UUID destinationHubId;
    private Long deliveryPersonId;
    private String deliveryPersonSlackId;
    private Integer estimatedTime;
    private Integer actualTime;
    private Double estimatedDistance;
    private List<UUID> routeSegments;

    public static DeliveryRouteDto from(DeliveryRoute route) {
        return DeliveryRouteDto.builder()
                .deliveryRouteId(route.getDeliveryRouteId())
                .originHubId(route.getOriginHubId())
                .destinationHubId(route.getDestinationHubId())
                .deliveryPersonId(route.getDeliveryPersonId())
                .deliveryPersonSlackId(route.getDeliveryPersonSlackId())
                .estimatedTime(route.getEstimatedTime())
                .estimatedDistance(route.getEstimatedDistance())
                .actualTime(route.getActualTime())
                .routeSegments(route.getRouteSegments())
                .build();
    }
}
