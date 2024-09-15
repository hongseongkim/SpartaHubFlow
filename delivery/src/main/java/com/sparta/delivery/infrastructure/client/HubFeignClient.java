package com.sparta.delivery.infrastructure.client;

import com.sparta.delivery.domain.route.hub.HubRouteResponseDto;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service", configuration = FeignConfig.class)
public interface HubFeignClient {

    @GetMapping("/api/v1/hubs/routes/{hubRouteId}")
    HubRouteResponseDto getHubRoute(@PathVariable UUID hubRouteId);

    @GetMapping("/api/v1/hubs/routes/origin/{originHubId}")
    List<HubRouteResponseDto> getHubRoutesByOrigin(@PathVariable UUID originHubId);

    @GetMapping("/api/v1/hubs/routes/destination/{destinationHubId}")
    List<HubRouteResponseDto> getHubRoutesByDestination(@PathVariable UUID destinationHubId);

    @GetMapping("/api/v1/hubs/routes")
    Page<HubRouteResponseDto> getAllHubRoutes(@SpringQueryMap Pageable pageable);
}
