package com.sparta.hub.domain.route.utils;

import com.sparta.hub.application.exception.ErrorCode;
import com.sparta.hub.application.exception.ServiceException;
import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.infrastructure.persistence.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RouteSegmentCalculator {

    private final HubRepository hubRepository;

    public List<UUID> calculateRouteSegments(UUID originHubId, UUID destinationHubId) {
        List<Hub> orderedHubs = hubRepository.findAllByOrderByCreatedAtAsc();
        List<UUID> routeSegments = new ArrayList<>();

        int startIndex = getHubIndex(orderedHubs, originHubId);
        int endIndex = getHubIndex(orderedHubs, destinationHubId);

        if (startIndex == -1 || endIndex == -1) {
            throw new ServiceException(ErrorCode.HUB_NOT_FOUND);
        }

        int step = Integer.compare(endIndex, startIndex);

        for (int i = startIndex; i != endIndex; i += step) {
            UUID currentHubId = orderedHubs.get(i).getHubId();
            routeSegments.add(currentHubId);
        }
        routeSegments.add(destinationHubId);

        return routeSegments;
    }

    private int getHubIndex(List<Hub> hubs, UUID hubId) {
        for (int i = 0; i < hubs.size(); i++) {
            if (hubs.get(i).getHubId().equals(hubId)) {
                return i;
            }
        }
        return -1;
    }
}
