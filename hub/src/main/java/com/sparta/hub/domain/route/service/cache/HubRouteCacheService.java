package com.sparta.hub.domain.route.service.cache;

import com.sparta.hub.application.exception.ErrorCode;
import com.sparta.hub.application.exception.ServiceException;
import com.sparta.hub.domain.route.dto.HubRoutePatchDto;
import com.sparta.hub.domain.route.model.HubRoute;
import com.sparta.hub.infrastructure.persistence.HubRouteRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubRouteCacheService {

    private final HubRouteRepository hubRouteJpaRepository;

    @Transactional
    public HubRoute saveHubRoute(HubRoute hubRoute) {
        return hubRouteJpaRepository.save(hubRoute);
    }

    @Transactional
    public void deleteHubRoute(UUID hubRouteId) {
        HubRoute route = hubRouteJpaRepository.findByHubRouteIdAndIsDeletedFalse(hubRouteId)
                .orElseThrow(() -> new ServiceException(ErrorCode.HUB_ROUTE_NOT_FOUND));
        route.softDelete();
        hubRouteJpaRepository.save(route);
    }

    @Transactional(readOnly = true)
    public HubRoute getHubRoute(UUID hubRouteId) {
        return hubRouteJpaRepository.findByHubRouteIdAndIsDeletedFalse(hubRouteId)
                .orElseThrow(() -> new ServiceException(ErrorCode.HUB_ROUTE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<HubRoute> getHubRoutesByOrigin(UUID originHubId) {
        return hubRouteJpaRepository.findByOriginHubIdAndIsDeletedFalse(originHubId)
                .orElseThrow(() -> new ServiceException(ErrorCode.HUB_ROUTE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<HubRoute> getHubRoutesByDestination(UUID destinationHubId) {
        return hubRouteJpaRepository.findByDestinationHubIdAndIsDeletedFalse(destinationHubId)
                .orElseThrow(() -> new ServiceException(ErrorCode.HUB_ROUTE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<HubRoute> getAllHubRoutes(Pageable pageable) {
        return hubRouteJpaRepository.findAllByIsDeletedFalse(pageable);
    }
}
