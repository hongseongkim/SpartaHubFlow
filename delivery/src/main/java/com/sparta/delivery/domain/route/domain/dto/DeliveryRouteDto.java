package com.sparta.delivery.domain.route.domain.dto;

import com.sparta.delivery.domain.route.domain.model.DeliveryRoute;
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
    private List<UUID> routeSegments;

    public static DeliveryRouteDto from(DeliveryRoute route) {
        return DeliveryRouteDto.builder()
                .deliveryRouteId(route.getDeliveryRouteId())
                .originHubId(route.getOriginHubId())
                .destinationHubId(route.getDestinationHubId())
                .deliveryPersonId(route.getDeliveryPersonId())
                .deliveryPersonSlackId(route.getDeliveryPersonSlackId())
                .estimatedTime(route.getEstimatedTime())
                .actualTime(route.getActualTime())
                .routeSegments(route.getRouteSegments())
                .build();
    }
}
