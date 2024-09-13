package com.sparta.route.domain.service.hubRoute;

import com.sparta.route.domain.dto.hub.HubDto;
import com.sparta.route.domain.dto.hubRoute.HubRouteDto;
import com.sparta.route.domain.service.hub.HubCacheService;
import com.sparta.route.domain.service.hubRoute.cache.HubRouteCacheService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubRouteService {

    private final HubCacheService hubCacheService;
    private final HubRouteCacheService hubRouteCacheService;

    public HubRouteDto createHubRoute(HubRouteDto hubRouteDto) {
        validateHubs(hubRouteDto.getOriginHubId(), hubRouteDto.getDestinationHubId());
        return hubRouteCacheService.saveHubRoute(hubRouteDto);
    }

    public HubRouteDto updateHubRoute(UUID originHubId, UUID destinationHubId, HubRouteDto updateData) {
        validateHubs(originHubId, destinationHubId);
        HubRouteDto updatedRoute = HubRouteDto.updateWith(originHubId, destinationHubId, updateData);
        return hubRouteCacheService.updateHubRoute(updatedRoute);
    }

    public void deleteHubRoute(UUID originHubId, UUID destinationHubId) {
        validateHubs(originHubId, destinationHubId);
        hubRouteCacheService.deleteHubRoute(originHubId, destinationHubId);
    }

    public HubRouteDto getHubRoute(UUID originHubId, UUID destinationHubId) {
        validateHubs(originHubId, destinationHubId);
        return hubRouteCacheService.getHubRoute(originHubId, destinationHubId);
    }

    public List<HubRouteDto> getHubRoutesByOrigin(UUID originHubId) {
        validateHub(originHubId);
        return hubRouteCacheService.getHubRoutesByOrigin(originHubId);
    }

    public List<HubRouteDto> getAllHubRoutes() {
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
        return hubCacheService.getHub(hubId) != null;
    }

    private String generateRouteDisplayName(HubDto originHub, HubDto destinationHub) {
        return originHub.getName() + " -> " + destinationHub.getName();
    } // 임시 로직

    public Integer calculateEstimatedTime(UUID originHubId, UUID destinationHubId) {
        validateHubs(originHubId, destinationHubId);
        return 30 + (int)(Math.random() * 90); // 임시 로직
    }
}
