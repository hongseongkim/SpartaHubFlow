package com.sparta.hub.domain.route.service;

import com.sparta.hub.application.exception.ErrorCode;
import com.sparta.hub.application.exception.ServiceException;
import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.domain.route.dto.HubRouteDto;
import com.sparta.hub.domain.route.model.HubRoute;
import com.sparta.hub.domain.route.service.cache.HubRouteCacheService;
import com.sparta.hub.domain.route.service.utils.RouteSegmentCalculator;
import com.sparta.hub.domain.route.service.utils.RouteTimeEstimator;
import com.sparta.hub.infrastructure.persistence.HubRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubRouteService {

    private final HubRepository hubRepository;
    private final HubRouteCacheService hubRouteCacheService;

    private final RouteTimeEstimator routeTimeEstimator;
    private final RouteSegmentCalculator routeSegmentCalculator;

    public HubRoute createHubRoute(UUID originHubId, UUID destinationHubId) {
        validateHubs(originHubId, destinationHubId);
        HubRoute hubRoute = HubRoute.create(originHubId, destinationHubId);

        List<UUID> calculatedRouteSegments = routeSegmentCalculator.calculateRouteSegments(
                hubRoute.getOriginHubId(), hubRoute.getDestinationHubId());
        hubRoute.updateRouteSegments(calculatedRouteSegments);

        int totalEstimatedTime = calculateTotalEstimatedTime(calculatedRouteSegments);
        String routeDisplayName = generateRouteDisplayName(hubRoute.getOriginHubId(), hubRoute.getDestinationHubId());
        hubRoute.updateHubRouteInfo(totalEstimatedTime, routeDisplayName);

        return hubRouteCacheService.saveHubRoute(hubRoute);
    }

    public HubRoute updateHubRoute(UUID hubRouteId, HubRouteDto hubRouteDto) {
        return hubRouteCacheService.updateHubRoute(hubRouteId, hubRouteDto);
    }

    public void deleteHubRoute(UUID hubRouteId) {
        hubRouteCacheService.deleteHubRoute(hubRouteId);
    }

    public HubRoute getHubRoute(UUID hubRouteId) {
        return hubRouteCacheService.getHubRoute(hubRouteId);
    }

    public List<HubRoute> getHubRoutesByOrigin(UUID originHubId) {
        validateHub(originHubId);
        return hubRouteCacheService.getHubRoutesByOrigin(originHubId);
    }

    public List<HubRoute> getHubRoutesByDestination(UUID destinationHubId) {
        validateHub(destinationHubId);
        return hubRouteCacheService.getHubRoutesByDestination(destinationHubId);
    }

    public Page<HubRoute> getAllHubRoutes(Pageable pageable) {
        return hubRouteCacheService.getAllHubRoutes(pageable);
    }

    private void validateHub(UUID hubId) {
        if (!hubRepository.existsById(hubId)) {
            throw new EntityNotFoundException("Hub not found with ID: " + hubId);
        }
    }

    private void validateHubs(UUID... hubIds) {
        for (UUID hubId : hubIds) {
            validateHub(hubId);
        }
    }

    private int calculateTotalEstimatedTime(List<UUID> routeSegments) {
        int totalEstimatedTime = 0;

        for (int i = 0; i < routeSegments.size() - 1; i++) {
            Hub currentHub = hubRepository.findById(routeSegments.get(i))
                    .orElseThrow(() -> new EntityNotFoundException("Hub not found"));
            Hub nextHub = hubRepository.findById(routeSegments.get(i + 1))
                    .orElseThrow(() -> new EntityNotFoundException("Hub not found"));

            totalEstimatedTime += routeTimeEstimator.calculateEstimatedTime(currentHub, nextHub);
        }
        return totalEstimatedTime;
    }

    private String generateRouteDisplayName(UUID originHubId, UUID destinationHubId) {
        Hub originHub = hubRepository.findById(originHubId)
                .orElseThrow(() -> new ServiceException(ErrorCode.HUB_NOT_FOUND));
        Hub destinationHub = hubRepository.findById(destinationHubId)
                .orElseThrow(() -> new ServiceException(ErrorCode.HUB_NOT_FOUND));
        return originHub.getName() + " -> " + destinationHub.getName();
    }
}
