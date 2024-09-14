package com.sparta.hub.domain.route.service.cache;

import com.sparta.hub.domain.route.dto.HubRouteDto;
import com.sparta.hub.domain.route.dto.HubRouteResponseDto;
import com.sparta.hub.domain.route.model.HubRoute;
import com.sparta.hub.infrastructure.persistence.HubRouteRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubRouteCacheService {

    private final HubRouteRepository hubRouteJpaRepository;

    @Transactional
    public HubRouteResponseDto saveHubRoute(HubRouteDto hubRouteDto) {
        HubRoute hubRoute = HubRouteDto.toEntity(hubRouteDto);
        HubRoute savedRoute = hubRouteJpaRepository.save(hubRoute);
        return HubRouteResponseDto.from(savedRoute);
    }

    @Transactional
    public HubRouteResponseDto updateHubRoute(HubRouteDto hubRouteDto) {
        HubRoute route = hubRouteJpaRepository.findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(
                        hubRouteDto.getOriginHubId(), hubRouteDto.getDestinationHubId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출발지와 목적지의 경로 정보를 찾을 수 없습니다."));

        route.updateHubRoute(hubRouteDto.getDestinationHubId(), hubRouteDto.getEstimatedTime(),
                hubRouteDto.getRouteDisplayName());

        HubRoute updatedRoute = hubRouteJpaRepository.save(route);
        return HubRouteResponseDto.from(updatedRoute);
    }

    @Transactional
    public void deleteHubRoute(UUID originHubId, UUID destinationHubId) {
        HubRoute route = hubRouteJpaRepository.findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(originHubId, destinationHubId)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 경로 정보를 찾을 수 없습니다."));
        route.softDelete();
        hubRouteJpaRepository.save(route);
    }

    @Transactional(readOnly = true)
    public HubRouteResponseDto getHubRoute(UUID originHubId, UUID destinationHubId) {
        HubRoute route = hubRouteJpaRepository.findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(originHubId, destinationHubId)
                .orElseThrow(() -> new EntityNotFoundException("요청한 경로 정보를 찾을 수 없습니다."));
        return HubRouteResponseDto.from(route);
    }

    @Transactional(readOnly = true)
    public List<HubRouteResponseDto> getHubRoutesByOrigin(UUID originHubId) {
        List<HubRoute> routes = hubRouteJpaRepository.findByOriginHubIdAndIsDeletedFalse(originHubId);
        return routes.stream()
                .map(HubRouteResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HubRouteResponseDto> getAllHubRoutes() {
        List<HubRoute> routes = hubRouteJpaRepository.findAllByIsDeletedFalse();
        return routes.stream()
                .map(HubRouteResponseDto::from)
                .toList();
    }
}
