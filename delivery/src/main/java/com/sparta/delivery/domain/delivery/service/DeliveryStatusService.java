package com.sparta.delivery.domain.delivery.service;

import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import com.sparta.delivery.infrastructure.persistence.DeliveryJpaRepository;
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

    private final DeliveryJpaRepository deliveryJpaRepository;

    @Transactional
    public void startDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryRouteEntity(deliveryId);
        delivery.startDelivery();
        deliveryJpaRepository.save(delivery);

        log.info("배송이 시작되었습니다. Delivery ID: {}", deliveryId);
    }

    @Transactional
    public void completeDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryRouteEntity(deliveryId);
        delivery.completeDelivery();
        deliveryJpaRepository.save(delivery);

        log.info("배송이 완료되었습니다. Delivery ID: {}, Actual Time: {} 분", deliveryId, delivery.getActualTime());
    }

    private Delivery getDeliveryRouteEntity(UUID deliveryId) {
        return deliveryJpaRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 Delivery를 찾을 수 없습니다: " + deliveryId));
    }
}
