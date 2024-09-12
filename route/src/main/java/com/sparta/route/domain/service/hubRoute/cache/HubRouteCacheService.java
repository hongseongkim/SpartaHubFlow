package com.sparta.route.domain.service.hubRoute.cache;

import com.sparta.route.domain.dto.hubRoute.HubRouteDto;
import com.sparta.route.domain.model.hubRoute.HubRoute;
import com.sparta.route.infrastructure.persistence.HubRouteJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HubRouteCacheService {

    private final HubRouteJpaRepository hubRouteJpaRepository;

    @Transactional
    public HubRouteDto saveHubRoute(HubRouteDto hubRouteDto) {
        HubRoute hubRoute = HubRouteDto.toEntity(hubRouteDto);
        HubRoute savedRoute = hubRouteJpaRepository.save(hubRoute);
        return HubRouteDto.from(savedRoute);
    }

    @Transactional
    public HubRouteDto updateHubRoute(HubRouteDto hubRouteDto) {
        HubRoute route = hubRouteJpaRepository.findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(
                        hubRouteDto.getOriginHubId(), hubRouteDto.getDestinationHubId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출발지와 목적지의 경로 정보를 찾을 수 없습니다."));

        route.updateHubRoute(hubRouteDto.getDestinationHubId(), hubRouteDto.getEstimatedTime(),
                hubRouteDto.getRouteDisplayName());

        HubRoute updatedRoute = hubRouteJpaRepository.save(route);
        return HubRouteDto.from(updatedRoute);
    }

    @Transactional
    public void deleteHubRoute(UUID originHubId, UUID destinationHubId) {
        HubRoute route = hubRouteJpaRepository.findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(originHubId, destinationHubId)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 경로 정보를 찾을 수 없습니다."));
        route.softDelete();
        hubRouteJpaRepository.save(route);
    }

    @Transactional(readOnly = true)
    public HubRouteDto getHubRoute(UUID originHubId, UUID destinationHubId) {
        HubRoute route = hubRouteJpaRepository.findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(originHubId, destinationHubId)
                .orElseThrow(() -> new EntityNotFoundException("요청한 경로 정보를 찾을 수 없습니다."));
        return HubRouteDto.from(route);
    }

    @Transactional(readOnly = true)
    public List<HubRouteDto> getHubRoutesByOrigin(UUID originHubId) {
        List<HubRoute> routes = hubRouteJpaRepository.findByOriginHubIdAndIsDeletedFalse(originHubId);
        return routes.stream()
                .map(HubRouteDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HubRouteDto> getAllHubRoutes() {
        List<HubRoute> routes = hubRouteJpaRepository.findAllByIsDeletedFalse();
        return routes.stream()
                .map(HubRouteDto::from)
                .toList();
    }
}
