package com.sparta.delivery.domain.service;

import com.sparta.delivery.domain.dto.DeliveryDto;
import com.sparta.delivery.domain.model.Delivery;
import com.sparta.delivery.infrastructure.persistence.DeliveryJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryCacheService {

    private final DeliveryJpaRepository deliveryJpaRepository;

    @Transactional
    public DeliveryDto saveDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = DeliveryDto.toEntity(deliveryDto);
        Delivery savedDelivery = deliveryJpaRepository.save(delivery);
        return DeliveryDto.from(savedDelivery);
    }

    @Transactional(readOnly = true)
    public Optional<Delivery> getDeliveryById(UUID deliveryId) {
        return deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId);
    }

    @Transactional(readOnly = true)
    public List<Delivery> getAllDeliveries() {
        return deliveryJpaRepository.findAllByIsDeletedFalse();
    }

    @Transactional
    public DeliveryDto updateDelivery(UUID deliveryId, DeliveryDto deliveryDto) {
        Delivery delivery = deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found with ID: " + deliveryId));

        delivery.updateDelivery(
                deliveryDto.getDepartureHubId(),
                deliveryDto.getDestinationHubId(),
                deliveryDto.getDeliveryAddress(),
                deliveryDto.getReceiverId(),
                deliveryDto.getReceiverSlackId()
        );

        Delivery updatedDelivery = deliveryJpaRepository.save(delivery);
        return DeliveryDto.from(updatedDelivery);
    }

    @Transactional
    public void deleteDelivery(UUID deliveryId) {
        Delivery delivery = deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found with ID: " + deliveryId));
        delivery.softDelete();
        deliveryJpaRepository.save(delivery);
    }
}
