package com.sparta.hub.domain.route.service.utils;

import com.sparta.hub.application.exception.ErrorCode;
import com.sparta.hub.application.exception.ServiceException;
import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.domain.route.model.RouteInfo;
import com.sparta.hub.infrastructure.persistence.HubRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteEstimatorImpl implements RouteCalculationService {

    private final HubRepository hubRepository;

    @Override
    public int calculateEstimatedTime(Hub originHub, Hub destinationHub) {
        if (originHub == null || destinationHub == null) {
            log.error("Origin or destination hub is null");
            throw new IllegalArgumentException("Origin and destination hubs must not be null");
        }

        try {
            double distance = calculateDistance(originHub, destinationHub);
            int baseTime = (int) (distance / 60.0 * 60); // 60km/h의 평균 속도 가정
            double trafficMultiplier = getTrafficMultiplier(LocalDateTime.now());
            int estimatedTime = (int) (baseTime * trafficMultiplier);

            // 최소 30분, 최대 8시간으로 제한
            return Math.max(30, Math.min(estimatedTime, 480));
        } catch (Exception e) {
            log.error("Error calculating estimated time: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate estimated time", e);
        }
    }

    @Override
    public double calculateDistance(Hub origin, Hub destination) {
        if (origin == null || destination == null) {
            log.error("Origin or destination hub is null");
            throw new IllegalArgumentException("Origin and destination hubs must not be null");
        }

        try {
            double earthRadius = 6371; // 지구의 반경 (km)
            double dLat = Math.toRadians(destination.getLatitude() - origin.getLatitude());
            double dLon = Math.toRadians(destination.getLongitude() - origin.getLongitude());
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(origin.getLatitude())) * Math.cos(Math.toRadians(destination.getLatitude())) *
                            Math.sin(dLon/2) * Math.sin(dLon/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            return earthRadius * c;
        } catch (Exception e) {
            log.error("Error calculating distance: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate distance", e);
        }
    }

    private double getTrafficMultiplier(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        if (hour >= 7 && hour <= 9) return 1.5; // 아침 러시아워
        if (hour >= 17 && hour <= 19) return 1.4; // 저녁 러시아워
        if (hour >= 22 || hour <= 5) return 0.8; // 심야 시간대
        return 1.0; // 그 외 시간대
    }

    @Override
    public List<UUID> calculateRouteSegments(UUID originHubId, UUID destinationHubId) {
        List<Hub> orderedHubs = hubRepository.findAllByOrderByCreatedAtAsc();

        int startIndex = getHubIndex(orderedHubs, originHubId);
        int endIndex = getHubIndex(orderedHubs, destinationHubId);

        if (startIndex == -1 || endIndex == -1) {
            throw new ServiceException(ErrorCode.HUB_NOT_FOUND);
        }

        return calculateRoute(orderedHubs, startIndex, endIndex);
    }

    private List<UUID> calculateRoute(List<Hub> hubs, int startIndex, int endIndex) {
        List<UUID> route = new ArrayList<>();
        int size = hubs.size();
        int current = startIndex;

        // 시작 인덱스가 종료 인덱스보다 큰 경우 역방향으로 계산
        if (startIndex > endIndex) {
            while (current != endIndex) {
                route.add(hubs.get(current).getHubId());
                current = (current - 1 + size) % size;  // 이전 허브로 이동 (순환 구조)
            }
        } else {
            // 정방향 계산
            while (current != endIndex) {
                route.add(hubs.get(current).getHubId());
                current = (current + 1) % size;  // 다음 허브로 이동 (순환 구조)
            }
        }

        route.add(hubs.get(endIndex).getHubId());  // 목적지 추가
        return route;
    }

    private int getHubIndex(List<Hub> hubs, UUID hubId) {
        for (int i = 0; i < hubs.size(); i++) {
            if (hubs.get(i).getHubId().equals(hubId)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public RouteInfo calculateRouteInfo(List<UUID> routeSegments, Map<UUID, Hub> hubMap) {
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

            totalEstimatedTime += calculateEstimatedTime(currentHub, nextHub);
            totalEstimatedDistance += calculateDistance(currentHub, nextHub);
        }

        return new RouteInfo(totalEstimatedTime, totalEstimatedDistance);
    }
}
