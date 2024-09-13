package com.sparta.delivery.domain.service;

import com.sparta.delivery.domain.dto.DeliveryDto;
import com.sparta.delivery.domain.model.Delivery;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryCacheService deliveryCacheService;

    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        return deliveryCacheService.saveDelivery(deliveryDto);
    }

    @Transactional
    public DeliveryDto updateDelivery(UUID deliveryId, DeliveryDto deliveryDto) {
        return deliveryCacheService.updateDelivery(deliveryId, deliveryDto);
    }

    @Transactional
    public void deleteDelivery(UUID deliveryId) {
        deliveryCacheService.deleteDelivery(deliveryId);
    }

    @Transactional(readOnly = true)
    public DeliveryDto getDelivery(UUID deliveryId) {
        return deliveryCacheService.getDeliveryById(deliveryId)
                .map(DeliveryDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found with ID: " + deliveryId));
    }

    @Transactional(readOnly = true)
    public List<DeliveryDto> getAllDeliveries() {
        return deliveryCacheService.getAllDeliveries().stream()
                .map(DeliveryDto::from)
                .collect(Collectors.toList());
    }
}
