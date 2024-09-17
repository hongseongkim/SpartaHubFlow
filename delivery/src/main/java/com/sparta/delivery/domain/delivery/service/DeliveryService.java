package com.sparta.delivery.domain.delivery.service;

import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryRequestDto;
import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryResponseDto;
import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import com.sparta.delivery.infrastructure.persistence.DeliveryJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryJpaRepository deliveryJpaRepository;
    private final DeliveryRouteService deliveryRouteService;

    @Transactional
    public DeliveryResponseDto createDelivery(DeliveryRequestDto deliveryRequestDto) {
        log.info("새로운 주문에 대한 배송 생성 중: {}", deliveryRequestDto.getOrderId());

        try {
            Delivery delivery = createAndSaveDelivery(deliveryRequestDto);
            DeliveryRoute route = deliveryRouteService.createAndSaveDeliveryRoute(deliveryRequestDto, delivery);

            delivery.updateRoute(route);
            delivery = deliveryJpaRepository.save(delivery);

            log.info("배송이 성공적으로 생성되었습니다. ID: {}", delivery.getDeliveryId());
            return DeliveryResponseDto.from(delivery, route);
        } catch (Exception e) {
            log.error("배송 생성에 실패했습니다: {}", e.getMessage());
            throw new ServiceException("배송 생성에 실패했습니다", e);
        }
    }

    private Delivery createAndSaveDelivery(DeliveryRequestDto deliveryRequestDto) {
        Delivery delivery = Delivery.create(
                deliveryRequestDto.getOrderId(),
                deliveryRequestDto.getDeliveryAddress(),
                deliveryRequestDto.getReceiverId(),
                deliveryRequestDto.getReceiverSlackId()
        );

        return deliveryJpaRepository.save(delivery);
    }

    @Transactional
    public DeliveryResponseDto updateDelivery(UUID deliveryId, DeliveryRequestDto deliveryRequestDto) {
        Delivery delivery = getDeliveryEntity(deliveryId);

        delivery.updateDeliveryInfo(
                deliveryRequestDto.getOrderId(),
                deliveryRequestDto.getDeliveryAddress(),
                deliveryRequestDto.getReceiverId(),
                deliveryRequestDto.getReceiverSlackId()
        );

        deliveryJpaRepository.save(delivery);

        log.info("배송이 성공적으로 업데이트되었습니다: {}", deliveryId);

        return DeliveryResponseDto.from(delivery, delivery.getRoute());
    }

    @Transactional
    public void deleteDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryEntity(deliveryId);
        delivery.softDelete();
        deliveryJpaRepository.save(delivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDto getDelivery(UUID deliveryId) {
        return DeliveryResponseDto.from(getDeliveryEntity(deliveryId), getDeliveryEntity(deliveryId).getRoute());
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponseDto> getAllDeliveries() {
        return deliveryJpaRepository.findAllByIsDeletedFalse().stream()
                .map(delivery -> DeliveryResponseDto.from(delivery, delivery.getRoute()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Delivery getDeliveryEntity(UUID deliveryId) {
        return deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 배송을 찾을 수 없습니다: " + deliveryId));
    }
}
