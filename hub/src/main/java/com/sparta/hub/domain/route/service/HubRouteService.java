package com.sparta.hub.domain.route.service;

import com.sparta.hub.application.exception.ErrorCode;
import com.sparta.hub.application.exception.ServiceException;
import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.domain.route.dto.HubRoutePatchDto;
import com.sparta.hub.domain.route.model.HubRoute;
import com.sparta.hub.domain.route.model.RouteInfo;
import com.sparta.hub.domain.route.service.cache.HubRouteCacheService;
import com.sparta.hub.domain.route.service.utils.RouteSegmentCalculator;
import com.sparta.hub.domain.route.service.utils.RouteEstimator;
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

    private final RouteEstimator routeEstimator;
    private final RouteSegmentCalculator routeSegmentCalculator;

    public HubRoute createHubRoute(UUID originHubId, UUID destinationHubId) {
        log.info("Creating hub route from {} to {}", originHubId, destinationHubId);

        List<UUID> calculatedRouteSegments;
        try {
            calculatedRouteSegments = routeSegmentCalculator.calculateRouteSegments(originHubId, destinationHubId);
            log.info("Calculated route segments: {}", calculatedRouteSegments);
        } catch (Exception e) {
            log.error("Failed to calculate route segments: {}", e.getMessage());
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Map<UUID, Hub> hubMap;
        try {
            hubMap = fetchRequiredHubs(calculatedRouteSegments);
        } catch (ServiceException e) {
            log.error("Failed to fetch required hubs: {}", e.getMessage());
            throw e;
        }

        HubRoute hubRoute = HubRoute.create(originHubId, destinationHubId);
        hubRoute.updateRouteSegments(calculatedRouteSegments);

        RouteInfo routeInfo;
        try {
            routeInfo = calculateRouteInfo(calculatedRouteSegments, hubMap);
        } catch (ServiceException e) {
            log.error("Failed to calculate route info: {}", e.getMessage());
            throw e;
        }

        String routeDisplayName = generateRouteDisplayName(originHubId, destinationHubId, hubMap);

        hubRoute.updateHubRouteInfo(routeInfo.getTotalEstimatedTime(),
                routeInfo.getTotalEstimatedDistance(),
                routeDisplayName);

        try {
            return hubRouteCacheService.saveHubRoute(hubRoute);
        } catch (Exception e) {
            log.error("Failed to save hub route: {}", e.getMessage());
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
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
            List<UUID> newRouteSegments;
            try {
                newRouteSegments = routeSegmentCalculator.calculateRouteSegments(newOriginHubId, newDestinationHubId);
                log.info("Calculated new route segments: {}", newRouteSegments);
            } catch (Exception e) {
                log.error("Failed to calculate new route segments: {}", e.getMessage());
                throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            Map<UUID, Hub> hubMap;
            try {
                hubMap = fetchRequiredHubs(newRouteSegments);
            } catch (ServiceException e) {
                log.error("Failed to fetch required hubs: {}", e.getMessage());
                throw e;
            }

            RouteInfo routeInfo;
            try {
                routeInfo = calculateRouteInfo(newRouteSegments, hubMap);
            } catch (ServiceException e) {
                log.error("Failed to calculate route info: {}", e.getMessage());
                throw e;
            }

            String newRouteDisplayName = generateRouteDisplayName(newOriginHubId, newDestinationHubId, hubMap);

            existingRoute.updateOriginAndDestination(newOriginHubId, newDestinationHubId);
            existingRoute.updateRouteSegments(newRouteSegments);
            existingRoute.updateHubRouteInfo(
                    routeInfo.getTotalEstimatedTime(),
                    routeInfo.getTotalEstimatedDistance(),
                    newRouteDisplayName
            );
        } else {
            log.info("Route not changed. Updating other information.");
            existingRoute.updateHubRouteInfo(
                    hubRoutePatchDto.getEstimatedTime(),
                    hubRoutePatchDto.getEstimatedDistance(),
                    hubRoutePatchDto.getRouteDisplayName()
            );
        }

        try {
            return hubRouteCacheService.saveHubRoute(existingRoute);
        } catch (Exception e) {
            log.error("Failed to save updated hub route: {}", e.getMessage());
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<UUID, Hub> fetchRequiredHubs(List<UUID> hubIds) {
        List<Hub> hubs = hubRepository.findAllById(hubIds);
        Map<UUID, Hub> hubMap = hubs.stream().collect(Collectors.toMap(Hub::getHubId, Function.identity()));

        Set<UUID> notFoundHubIds = new HashSet<>(hubIds);
        notFoundHubIds.removeAll(hubMap.keySet());

        if (!notFoundHubIds.isEmpty()) {
            log.error("다음 ID를 가진 허브를 찾을 수 없습니다: {}", notFoundHubIds);
            throw new ServiceException(ErrorCode.HUB_NOT_FOUND);
        }

        return hubMap;
    }

    private RouteInfo calculateRouteInfo(List<UUID> routeSegments, Map<UUID, Hub> hubMap) {
        int totalEstimatedTime = 0;
        double totalEstimatedDistance = 0;

        for (int i = 0; i < routeSegments.size() - 1; i++) {
            UUID currentHubId = routeSegments.get(i);
            UUID nextHubId = routeSegments.get(i + 1);

            Hub currentHub = hubMap.get(currentHubId);
            Hub nextHub = hubMap.get(nextHubId);

            if (currentHub == null || nextHub == null) {
                log.error("Hub not found. Current hub ID: {}, Next hub ID: {}", currentHubId, nextHubId);
                throw new ServiceException(ErrorCode.HUB_NOT_FOUND);
            }

            totalEstimatedTime += routeEstimator.calculateEstimatedTime(currentHub, nextHub);
            totalEstimatedDistance += routeEstimator.calculateDistance(currentHub, nextHub);
        }

        return new RouteInfo(totalEstimatedTime, totalEstimatedDistance);
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

    private String generateRouteDisplayName(UUID originHubId, UUID destinationHubId, Map<UUID, Hub> hubMap) {
        Hub originHub = hubMap.get(originHubId);
        Hub destinationHub = hubMap.get(destinationHubId);

        if (originHub == null || destinationHub == null) {
            throw new ServiceException(ErrorCode.HUB_NOT_FOUND);
        }

        return originHub.getName() + " -> " + destinationHub.getName();
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
