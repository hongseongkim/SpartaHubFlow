package com.sparta.delivery.domain.route.hub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HubRouteResponseDto {
    private UUID hubRouteId;
    private UUID originHubId;
    private UUID destinationHubId;
    private Integer estimatedTime;
    private Double estimatedDistance;
    private String routeDisplayName;
    private List<UUID> routeSegments;
}
