package com.sparta.hub.domain.route.dto;

import java.util.UUID;
import lombok.Getter;

@Getter
public class HubRoutePatchDto {
    private UUID originHubId;
    private UUID destinationHubId;
    private Integer estimatedTime;
    private Double estimatedDistance;
    private String routeDisplayName;
}
