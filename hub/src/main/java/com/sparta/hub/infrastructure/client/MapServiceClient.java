package com.sparta.hub.infrastructure.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "map-service")
public interface MapServiceClient {
    @GetMapping("/api/v1/maps/coordinates")
    Map<String, Double> getCoordinates(@RequestParam("address") String address);
}
