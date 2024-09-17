package com.sparta.hub.domain.route.service;

import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.domain.route.dto.HubRoutePatchDto;
import com.sparta.hub.domain.route.model.HubRoute;
import com.sparta.hub.domain.route.model.RouteInfo;
import com.sparta.hub.domain.route.service.cache.HubRouteCacheService;
import com.sparta.hub.domain.route.service.utils.RouteCalculationService;
import com.sparta.hub.infrastructure.persistence.HubRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubRouteService {

    private final HubRepository hubRepository;
    private final HubRouteCacheService hubRouteCacheService;
    private final RouteCalculationService routeCalculationService;

    public HubRoute createHubRoute(UUID originHubId, UUID destinationHubId) {
        log.info("Creating hub route from {} to {}", originHubId, destinationHubId);
        HubRoute hubRoute = HubRoute.create(originHubId, destinationHubId);
        return updateRouteInformation(hubRoute, originHubId, destinationHubId);
    }

    @Transactional
    public HubRoute updateHubRoute(UUID hubRouteId, HubRoutePatchDto hubRoutePatchDto) {
        log.info("Updating hub route: {}", hubRouteId);
        HubRoute existingRoute = getHubRoute(hubRouteId);

        UUID newOriginHubId = hubRoutePatchDto.getOriginHubId();
        UUID newDestinationHubId = hubRoutePatchDto.getDestinationHubId();

        boolean isRouteChanged = !existingRoute.getOriginHubId().equals(newOriginHubId)
                || !existingRoute.getDestinationHubId().equals(newDestinationHubId);

        if (isRouteChanged) {
            log.info("Route changed. Recalculating route segments.");
            return updateRouteInformation(existingRoute, newOriginHubId, newDestinationHubId);
        } else {
            log.info("Route not changed. Updating other information.");
            existingRoute.updateHubRouteInfo(
                    hubRoutePatchDto.getEstimatedTime(),
                    hubRoutePatchDto.getEstimatedDistance(),
                    hubRoutePatchDto.getRouteDisplayName()
            );
            return hubRouteCacheService.saveHubRoute(existingRoute);
        }
    }

    private HubRoute updateRouteInformation(HubRoute hubRoute, UUID originHubId, UUID destinationHubId) {
        List<UUID> calculatedRouteSegments = calculateRouteSegments(originHubId, destinationHubId);
        Map<UUID, Hub> hubMap = fetchRequiredHubs(calculatedRouteSegments);
        RouteInfo routeInfo = calculateRouteInfo(calculatedRouteSegments, hubMap);
        String routeDisplayName = generateRouteDisplayName(originHubId, destinationHubId, hubMap);

        hubRoute.updateOriginAndDestination(originHubId, destinationHubId);
        hubRoute.updateRouteSegments(calculatedRouteSegments);
        hubRoute.updateHubRouteInfo(
                routeInfo.getTotalEstimatedTime(),
                routeInfo.getTotalEstimatedDistance(),
                routeDisplayName
        );

        return hubRouteCacheService.saveHubRoute(hubRoute);
    }

    private List<UUID> calculateRouteSegments(UUID originHubId, UUID destinationHubId) {
        return routeCalculationService.calculateRouteSegments(originHubId, destinationHubId);
    }

    private Map<UUID, Hub> fetchRequiredHubs(List<UUID> hubIds) {

        validateHubs(hubIds.toArray(UUID[]::new));

        List<Hub> hubs = hubRepository.findByIdsAndIsDeletedFalse(hubIds);
        Map<UUID, Hub> hubMap = hubs.stream()
                .collect(Collectors.toMap(Hub::getHubId, Function.identity()));

        // 조회된 허브 수와 요청된 ID 수가 일치하는지 확인
        if (hubMap.size() != hubIds.size()) {
            log.warn("Number of fetched hubs ({}) does not match the number of requested IDs ({})",
                    hubMap.size(), hubIds.size());
            Set<UUID> missingHubIds = new HashSet<>(hubIds);
            missingHubIds.removeAll(hubMap.keySet());
            throw new EntityNotFoundException("Some hubs were not found or are deleted: " + missingHubIds);
        }

        return hubMap;
    }

    private RouteInfo calculateRouteInfo(List<UUID> routeSegments, Map<UUID, Hub> hubMap) {
        return routeCalculationService.calculateRouteInfo(routeSegments, hubMap);
    }

    private String generateRouteDisplayName(UUID originHubId, UUID destinationHubId, Map<UUID, Hub> hubMap) {
        Hub originHub = hubMap.get(originHubId);
        Hub destinationHub = hubMap.get(destinationHubId);
        return String.format("%s → %s", originHub.getName(), destinationHub.getName());
    }

    public void deleteHubRoute(UUID hubRouteId) {
        hubRouteCacheService.deleteHubRoute(hubRouteId);
    }

    public HubRoute getHubRoute(UUID hubRouteId) {
        return hubRouteCacheService.getHubRoute(hubRouteId);
    }

    public HubRoute getHubRouteByOriginAndDestination(UUID originHubId, UUID destinationHubId) {
        validateHubs(originHubId, destinationHubId);
        return hubRouteCacheService.getHubRoutesByOriginAndDestination(originHubId, destinationHubId);
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
}
