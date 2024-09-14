package com.sparta.route.domain.service.deliveryRoute;

import com.sparta.route.domain.dto.deliveryRoute.DeliveryRouteDto;
import com.sparta.route.domain.dto.deliveryRoute.DeliveryRouteRequestDto;
import com.sparta.route.domain.model.deliveryRoute.DeliveryRoute;
import com.sparta.route.infrastructure.persistence.DeliveryRouteJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryRouteService {

    private final DeliveryRouteJpaRepository deliveryRouteJpaRepository;

    @Transactional
    public void createDeliveryRoute(UUID deliveryId, List<DeliveryRouteRequestDto> routeDtos) {
        List<DeliveryRoute> routes = routeDtos.stream()
                .map(DeliveryRouteRequestDto::toDTO)
                .map(DeliveryRouteDto::toEntity)


                .collect(Collectors.toList());

        deliveryRouteJpaRepository.saveAll(routes);
    }

    public List<DeliveryRouteDto> getRoutesForDelivery(UUID deliveryId) {
        List<DeliveryRoute> routes = deliveryRouteJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId);
        return routes.stream()
                .map(DeliveryRouteDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateRoute(UUID routeId, DeliveryRouteRequestDto requestDto) {
        DeliveryRoute route = deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryRoute not found"));

        route.updateRouteInfo(requestDto.getOriginHubId(), requestDto.getDestinationHubId(),
                requestDto.getDeliveryPersonId());

        route.updateEstimations(requestDto.getEstimatedDistance(), requestDto.getEstimatedTime());

        deliveryRouteJpaRepository.save(route);
    }

    @Transactional
    public void deleteRoute(UUID routeId) {
        DeliveryRoute route = deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryRoute not found"));
        route.softDelete();
        deliveryRouteJpaRepository.save(route);
    }
}
