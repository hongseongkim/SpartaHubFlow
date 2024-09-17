package com.sparta.delivery.domain.delivery.service;

import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import com.sparta.delivery.infrastructure.persistence.DeliveryRouteJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryStatusService {

    private final DeliveryRouteJpaRepository deliveryRouteJpaRepository;

    @Transactional
    public void startDelivery(UUID deliveryId) {
        DeliveryRoute deliveryRoute = getDeliveryRouteEntity(deliveryId);
        deliveryRoute.startDelivery();
        deliveryRouteJpaRepository.save(deliveryRoute);

        log.info("배송이 시작되었습니다. Delivery ID: {}", deliveryId);
    }

    @Transactional
    public void completeDelivery(UUID deliveryId) {
        DeliveryRoute deliveryRoute = getDeliveryRouteEntity(deliveryId);
        deliveryRoute.completeDelivery();
        deliveryRouteJpaRepository.save(deliveryRoute);

        log.info("배송이 완료되었습니다. Delivery ID: {}, Actual Time: {} 분", deliveryId, deliveryRoute.getActualTime());
    }

    private DeliveryRoute getDeliveryRouteEntity(UUID routeId) {
        return deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 DeliveryRoute를 찾을 수 없습니다: " + routeId));
    }

}
