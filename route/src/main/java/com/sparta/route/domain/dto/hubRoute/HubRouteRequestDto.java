package com.sparta.route.domain.dto.hubRoute;

import com.sparta.route.domain.model.hubRoute.HubRoute;
import java.util.UUID;
import lombok.Getter;

@Getter
public class HubRouteRequestDto {
    private UUID originHubId;
    private UUID destinationHubId;
    private Integer estimatedTime;
    private String routeDisplayName;

    public HubRouteDto toDTO() {
        return HubRouteDto.create(originHubId, destinationHubId, estimatedTime, routeDisplayName);
    }
}
