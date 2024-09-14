package com.sparta.hub.domain.route.controller;

import com.sparta.hub.domain.route.dto.HubRouteRequestDto;
import com.sparta.hub.domain.route.dto.HubRouteResponseDto;
import com.sparta.hub.domain.route.service.HubRouteService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/hubs/routes")
public class HubRouteController {

    private final HubRouteService hubRouteService;

    @PostMapping
    public ResponseEntity<HubRouteResponseDto> createHubRoute(@Valid @RequestBody HubRouteRequestDto hubRouteRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hubRouteService.createHubRoute(hubRouteRequestDto.toDTO()));
    }

    @PutMapping("/{originHubId}/-/{destinationHubId}")
    public ResponseEntity<HubRouteResponseDto> updateHubRoute(
            @PathVariable UUID originHubId,
            @PathVariable UUID destinationHubId,
            @Valid @RequestBody HubRouteRequestDto hubRouteRequestDto) {
        return ResponseEntity.ok(hubRouteService.updateHubRoute(
                originHubId, destinationHubId, hubRouteRequestDto.toDTO()));
    }

    @DeleteMapping("/{originHubId}/-/{destinationHubId}")
    public ResponseEntity<Void> deleteHubRoute(@PathVariable UUID originHubId, @PathVariable UUID destinationHubId) {
        hubRouteService.deleteHubRoute(originHubId, destinationHubId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{originHubId}/-/{destinationHubId}")
    public ResponseEntity<HubRouteResponseDto> getHubRoute(@PathVariable UUID originHubId, @PathVariable UUID destinationHubId) {
        return ResponseEntity.ok(hubRouteService.getHubRoute(originHubId, destinationHubId));
    }

    @GetMapping("/origin/{originHubId}")
    public ResponseEntity<List<HubRouteResponseDto>> getHubRoutesByOrigin(@PathVariable UUID originHubId) {
        return ResponseEntity.ok(hubRouteService.getHubRoutesByOrigin(originHubId));
    }

    @GetMapping
    public ResponseEntity<List<HubRouteResponseDto>> getAllHubRoutes() {
        return ResponseEntity.ok(hubRouteService.getAllHubRoutes());
    }
}
