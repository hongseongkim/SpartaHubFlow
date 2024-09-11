package com.sparta.route.infrastructure.client;

import com.sparta.route.domain.dto.HubResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "hub-service", configuration = FeignConfig.class)
public interface HubFeignClient {
    @GetMapping("/api/v1/hubs/full-list")
    List<HubResponseDto> getAllHubs();
}
