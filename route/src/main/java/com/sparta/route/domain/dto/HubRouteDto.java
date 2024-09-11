package com.sparta.route.domain.dto;

import com.sparta.route.domain.model.HubRoute;
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

    public static HubRouteDto from(HubRoute hubRoute) {
        return HubRouteDto.builder()
                .originHubId(hubRoute.getOriginHubId())
                .destinationHubId(hubRoute.getDestinationHubId())
                .estimatedTime(hubRoute.getEstimatedTime())
                .routeDisplayName(hubRoute.getRouteDisplayName())
                .build();
    }

    public static HubRoute toEntity(HubRouteDto dto) {
        return HubRoute.create(dto.getOriginHubId(), dto.getDestinationHubId());
    }

}
