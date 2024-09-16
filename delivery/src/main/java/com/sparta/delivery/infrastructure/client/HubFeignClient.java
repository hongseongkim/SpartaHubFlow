package com.sparta.delivery.infrastructure.client;

import com.sparta.delivery.domain.delivery.dto.hub.HubRouteResponseDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service", configuration = FeignConfig.class)
public interface HubFeignClient {
    @GetMapping("api/v1/hubs/routes/{originHubId}/{destinationHubId}")
    HubRouteResponseDto getRoute(@PathVariable UUID originHubId, @PathVariable UUID destinationHubId);

}
