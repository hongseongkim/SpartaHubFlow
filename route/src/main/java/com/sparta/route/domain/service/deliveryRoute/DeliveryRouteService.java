package com.sparta.route.domain.service.deliveryRoute;

import com.sparta.route.domain.dto.deliveryRoute.DeliveryRouteDto;
import com.sparta.route.domain.model.deliveryRoute.DeliveryRoute;
import com.sparta.route.domain.model.deliveryRoute.enums.DeliveryStatus;
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
    public void createDeliveryRoute(UUID deliveryId, List<DeliveryRouteDto> routeDtos) {
        List<DeliveryRoute> routes = routeDtos.stream()
                .map(dto -> {
                    DeliveryRoute route = DeliveryRouteDto.toEntity(dto);
                    route.setDeliveryId(deliveryId);
                    route.setCurrentStatus(DeliveryStatus.WAITING_FOR_HUB_TRANSFER);
                    return route;
                })
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
    public void updateRouteStatus(UUID routeId, DeliveryStatus newStatus) {
        DeliveryRoute route = deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryRoute not found"));
        route.setCurrentStatus(newStatus);
        deliveryRouteJpaRepository.save(route);
    }

    @Transactional
    public void deleteRoute(UUID routeId) {
        DeliveryRoute route = deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryRoute not found"));
        route.setIsDeleted(true);
        deliveryRouteJpaRepository.save(route);
    }
}
