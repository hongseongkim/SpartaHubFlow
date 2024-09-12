package com.sparta.route.domain.dto.hubRoute;

import com.sparta.route.domain.model.hubRoute.HubRoute;
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

    public static HubRouteDto create(UUID originHubId, UUID destinationHubId, Integer estimatedTime, String routeDisplayName) {
        return new HubRouteDto(originHubId, destinationHubId, estimatedTime, routeDisplayName);
    }

    public static HubRouteDto from(HubRoute hubRoute) {
        return HubRouteDto.builder()
                .originHubId(hubRoute.getOriginHubId())
                .destinationHubId(hubRoute.getDestinationHubId())
                .estimatedTime(hubRoute.getEstimatedTime())
                .routeDisplayName(hubRoute.getRouteDisplayName())
                .build();
    }

    public static HubRoute toEntity(HubRouteDto dto) {
        return HubRoute.create(dto.getOriginHubId(), dto.getDestinationHubId(), dto.getEstimatedTime(),
                dto.getRouteDisplayName());
    }

    public static HubRouteDto updateWith(UUID originHubId, UUID destinationHubId, HubRouteDto updateData) {
        return HubRouteDto.builder()
                .originHubId(originHubId)
                .destinationHubId(destinationHubId)
                .estimatedTime(updateData.getEstimatedTime())
                .routeDisplayName(updateData.getRouteDisplayName())
                .build();
    }

}
