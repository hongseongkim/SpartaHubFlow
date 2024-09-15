package com.sparta.hub.domain.route.dto;

import java.util.UUID;
import lombok.Getter;

@Getter
public class HubRouteRequestDto {
    private UUID originHubId;
    private UUID destinationHubId;
}
