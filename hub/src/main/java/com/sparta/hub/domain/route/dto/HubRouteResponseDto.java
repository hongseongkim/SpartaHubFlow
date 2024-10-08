package com.sparta.hub.domain.route.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sparta.hub.domain.route.model.HubRoute;
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

    public static HubRouteResponseDto from(HubRoute hubRoute) {
        return HubRouteResponseDto.builder()
                .hubRouteId(hubRoute.getHubRouteId())
                .originHubId(hubRoute.getOriginHubId())
                .destinationHubId(hubRoute.getDestinationHubId())
                .estimatedTime(hubRoute.getEstimatedTime())
                .estimatedDistance(hubRoute.getEstimatedDistance())
                .routeDisplayName(hubRoute.getRouteDisplayName())
                .routeSegments(hubRoute.getRouteSegments())
                .build();
    }



}
