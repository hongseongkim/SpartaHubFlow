package com.sparta.route.domain.service;

import com.sparta.route.domain.dto.HubRouteDto;
import com.sparta.route.domain.model.HubRoute;
import com.sparta.route.infrastructure.persistence.HubRouteJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubRouteService {

    private final HubRouteJpaRepository hubRouteJpaRepository;

    @Transactional
    @CachePut(value = "hubRoutes", key = "#result.originHubId + '-' + #result.destinationHubId")
    public HubRouteDto createHubRoute(HubRouteDto hubRouteDto) {
        HubRoute hubRoute = HubRouteDto.toEntity(hubRouteDto);
        HubRoute savedRoute = hubRouteJpaRepository.save(hubRoute);
        return HubRouteDto.from(savedRoute);
    }

    @Transactional
    @CachePut(value = "hubRoutes", key = "#result.originHubId + '-' + #result.destinationHubId")
    public HubRouteDto updateHubRoute(UUID originHubId, UUID destinationHubId, HubRouteDto hubRouteDto) {
        HubRoute route = hubRouteJpaRepository.findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(originHubId, destinationHubId)
                .orElseThrow(() -> new EntityNotFoundException("경로 정보를 찾을 수 없습니다."));

        route.updateEstimatedTime(hubRouteDto.getEstimatedTime());
        route.updateRouteDisplayName(hubRouteDto.getRouteDisplayName());

        HubRoute updatedRoute = hubRouteJpaRepository.save(route);

        return HubRouteDto.from(updatedRoute);
    }

    @Transactional
    @CacheEvict(value = "hubRoutes", key = "#originHubId + '-' + #destinationHubId")
    public void deleteHubRoute(UUID originHubId, UUID destinationHubId) {
        HubRoute route = hubRouteJpaRepository.findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(originHubId, destinationHubId)
                .orElseThrow(() -> new EntityNotFoundException("경로 정보를 찾을 수 없습니다."));
        route.softDelete();
        hubRouteJpaRepository.save(route);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "hubRoutes", key = "#originHubId + '-' + #destinationHubId")
    public HubRouteDto getHubRoute(UUID originHubId, UUID destinationHubId) {
        HubRoute route = hubRouteJpaRepository.findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(originHubId, destinationHubId)
                .orElseThrow(() -> new EntityNotFoundException("경로 정보를 찾을 수 없습니다."));
        return HubRouteDto.from(route);
    }

    @Transactional(readOnly = true)
    public List<HubRouteDto> getAllHubRoutes() {
        List<HubRoute> routes = hubRouteJpaRepository.findAllByIsDeletedFalse();
        return routes.stream()
                .map(HubRouteDto::from)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "hubRoutesByOrigin", key = "#originHubId")
    public List<HubRouteDto> getHubRoutesByOrigin(UUID originHubId) {
        List<HubRoute> routes = hubRouteJpaRepository.findByOriginHubIdAndIsDeletedFalse(originHubId);
        return routes.stream()
                .map(HubRouteDto::from)
                .collect(Collectors.toList());
    }

    public Integer calculateEstimatedTime(UUID originHubId, UUID destinationHubId) {
        // 임시로 랜덤한 시간을 반환하는 예시:
        return 30 + (int)(Math.random() * 90); // 30분에서 120분 사이의 랜덤 시간
    }
}
