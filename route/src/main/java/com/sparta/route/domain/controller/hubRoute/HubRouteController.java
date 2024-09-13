package com.sparta.route.domain.controller.hubRoute;

import com.sparta.route.domain.dto.hubRoute.HubRouteDto;
import com.sparta.route.domain.dto.hubRoute.HubRouteRequestDto;
import com.sparta.route.domain.service.hubRoute.HubRouteService;
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
    public ResponseEntity<HubRouteDto> createHubRoute(@Valid @RequestBody HubRouteRequestDto hubRouteRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hubRouteService.createHubRoute(hubRouteRequestDto.toDTO()));
    }

    @PutMapping("/{originHubId}/-/{destinationHubId}")
    public ResponseEntity<HubRouteDto> updateHubRoute(
            @PathVariable UUID originHubId,
            @PathVariable UUID destinationHubId,
            @Valid @RequestBody HubRouteRequestDto hubRouteRequestDto) {
        return ResponseEntity.ok(hubRouteService.updateHubRoute(originHubId, destinationHubId,
                hubRouteRequestDto.toDTO()));
    }

    @DeleteMapping("/{originHubId}/-/{destinationHubId}")
    public ResponseEntity<Void> deleteHubRoute(@PathVariable UUID originHubId, @PathVariable UUID destinationHubId) {
        hubRouteService.deleteHubRoute(originHubId, destinationHubId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{originHubId}/-/{destinationHubId}")
    public ResponseEntity<HubRouteDto> getHubRoute(@PathVariable UUID originHubId, @PathVariable UUID destinationHubId) {
        return ResponseEntity.ok(hubRouteService.getHubRoute(originHubId, destinationHubId));
    }

    @GetMapping("/origin/{originHubId}")
    public ResponseEntity<List<HubRouteDto>> getHubRoutesByOrigin(@PathVariable UUID originHubId) {
        return ResponseEntity.ok(hubRouteService.getHubRoutesByOrigin(originHubId));
    }

    @GetMapping
    public ResponseEntity<List<HubRouteDto>> getAllHubRoutes() {
        return ResponseEntity.ok(hubRouteService.getAllHubRoutes());
    }
}
