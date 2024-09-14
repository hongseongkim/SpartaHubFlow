package com.sparta.hub.domain.route.dto;

import com.sparta.hub.domain.route.model.HubRoute;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class HubRouteDto {

    private UUID originHubId;
    private UUID destinationHubId;
    private Integer estimatedTime;
    private String routeDisplayName;
    private List<UUID> routeSegments;

    public static HubRouteDto create(UUID originHubId, UUID destinationHubId) {
        return new HubRouteDto(originHubId, destinationHubId, null, null, null);
    }
}
