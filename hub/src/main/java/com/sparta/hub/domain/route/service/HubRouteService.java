package com.sparta.hub.domain.route.service;

import com.sparta.hub.domain.hub.service.HubService;
import com.sparta.hub.domain.route.dto.HubRouteDto;
import com.sparta.hub.domain.route.dto.HubRouteResponseDto;
import com.sparta.hub.domain.route.service.cache.HubRouteCacheService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubRouteService {

    public final HubService hubService;
    private final HubRouteCacheService hubRouteCacheService;

    public HubRouteResponseDto createHubRoute(HubRouteDto hubRouteDto) {
        validateHubs(hubRouteDto.getOriginHubId(), hubRouteDto.getDestinationHubId());
        return hubRouteCacheService.saveHubRoute(hubRouteDto);
    }

    public HubRouteResponseDto updateHubRoute(UUID originHubId, UUID destinationHubId, HubRouteDto updateData) {
        validateHubs(originHubId, destinationHubId);
        HubRouteDto updatedRoute = HubRouteDto.updateWith(originHubId, destinationHubId, updateData);
        return hubRouteCacheService.updateHubRoute(updatedRoute);
    }

    public void deleteHubRoute(UUID originHubId, UUID destinationHubId) {
        validateHubs(originHubId, destinationHubId);
        hubRouteCacheService.deleteHubRoute(originHubId, destinationHubId);
    }

    public HubRouteResponseDto getHubRoute(UUID originHubId, UUID destinationHubId) {
        validateHubs(originHubId, destinationHubId);
        return hubRouteCacheService.getHubRoute(originHubId, destinationHubId);
    }

    public List<HubRouteResponseDto> getHubRoutesByOrigin(UUID originHubId) {
        validateHub(originHubId);
        return hubRouteCacheService.getHubRoutesByOrigin(originHubId);
    }

    public List<HubRouteResponseDto> getAllHubRoutes() {
        return hubRouteCacheService.getAllHubRoutes();
    }

    private void validateHub(UUID hubId) {
        if (!hubExists(hubId)) {
            throw new EntityNotFoundException("Origin Hub not found with ID: " + hubId);
        }
    }

    private void validateHubs(UUID originHubId, UUID destinationHubId) {
        if (!hubExists(originHubId)) {
            throw new EntityNotFoundException("Origin Hub not found with ID: " + originHubId);
        }
        if (!hubExists(destinationHubId)) {
            throw new EntityNotFoundException("Destination Hub not found with ID: " + destinationHubId);
        }
    }

    public boolean hubExists(UUID hubId) {
        return hubService.getHubById(hubId) != null;
    }

    public String generateRouteDisplayName(String originHubName, String destinationHubName) {
        return originHubName + " -> " + destinationHubName;
    }

    public Integer calculateEstimatedTime(UUID originHubId, UUID destinationHubId) {
        validateHubs(originHubId, destinationHubId);
        return 30 + (int)(Math.random() * 90); // 임시 로직
    }
}
