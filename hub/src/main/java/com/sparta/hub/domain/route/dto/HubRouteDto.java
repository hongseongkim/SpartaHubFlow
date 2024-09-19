package com.sparta.hub.domain.route.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class HubRouteDto {
    private UUID originHubId;
    private UUID destinationHubId;
    private Integer estimatedTime;
    private Double estimatedDistance;
    private String routeDisplayName;
    private List<UUID> routeSegments;
}
