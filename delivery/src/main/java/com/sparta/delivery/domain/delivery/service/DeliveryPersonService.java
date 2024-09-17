package com.sparta.delivery.domain.delivery.service;

import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryResponseDto;
import com.sparta.delivery.domain.delivery.dto.person.DeliveryPersonRequestDto;
import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.delivery.model.DeliveryPerson;
import com.sparta.delivery.domain.delivery.model.enums.DeliveryPersonType;
import com.sparta.delivery.infrastructure.persistence.DeliveryJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryPersonService {

    private final DeliveryJpaRepository deliveryJpaRepository;

    @Transactional
    public DeliveryResponseDto assignDeliveryPerson(UUID deliveryId, DeliveryPersonRequestDto deliveryPersonRequestDto) {

        Delivery delivery = getDeliveryEntity(deliveryId);
        DeliveryPerson deliveryPerson = getDeliveryPersonEntity(delivery, deliveryPersonRequestDto);

        if (deliveryPerson.getType() == DeliveryPersonType.HUB_MOVEMENT) {
            if (!isHubToHubTransfer(delivery)) {
                throw new ServiceException("허브 이동 담당자는 업체 배송을 할 수 없습니다.");
            }
        } else if (deliveryPerson.getType() == DeliveryPersonType.COMPANY_DELIVERY && isHubToHubTransfer(delivery)) {
            throw new ServiceException("업체 배송 담당자는 허브 간 이동을 할 수 없습니다.");
        }

        delivery.updateDeliveryPerson(deliveryPerson);
        deliveryJpaRepository.save(delivery);

        log.info("배송 담당자가 할당되었습니다: {}", deliveryPerson.getDeliveryPersonId());
        return DeliveryResponseDto.from(delivery, delivery.getRoute());
    }

    private boolean isHubToHubTransfer(Delivery delivery) {

        return delivery.getRoute().getRouteSegments().stream()
                .allMatch(segment -> segment.getHubId() != null && segment.getAddress() == null);
    }

    private DeliveryPerson getDeliveryPersonEntity(Delivery delivery, DeliveryPersonRequestDto deliveryPersonRequestDto) {
        // TODO: 실제 배송 담당자 정보를 조회하는 로직 추가
        return DeliveryPerson.create(
                deliveryPersonRequestDto.getUserId(),
                deliveryPersonRequestDto.getDeliveryPersonSlackId(),
                deliveryPersonRequestDto.getDeliveryPersonType(),
                delivery
        );
    }

    private Delivery getDeliveryEntity(UUID deliveryId) {
        return deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId).orElseThrow(
                EntityNotFoundException::new
        );
    }
}
