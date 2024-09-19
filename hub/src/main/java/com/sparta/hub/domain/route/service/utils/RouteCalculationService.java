package com.sparta.hub.domain.route.service.utils;

import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.domain.route.model.RouteInfo;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RouteCalculationService {
    int calculateEstimatedTime(Hub originHub, Hub destinationHub);
    double calculateDistance(Hub origin, Hub destination);
    List<UUID> calculateRouteSegments(UUID originHubId, UUID destinationHubId);
    RouteInfo calculateRouteInfo(List<UUID> routeSegments, Map<UUID, Hub> hubMap);
}
