package com.sparta.delivery.domain.delivery.dto.route;

import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class DeliveryRouteRequestDto {
    private UUID originHubId;
    private UUID destinationHubId;
    private Integer estimatedTime;
    private Double estimatedDistance;
    private List<UUID> routeSegments;
}
