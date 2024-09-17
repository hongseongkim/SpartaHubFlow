package com.sparta.delivery.domain.delivery.service;

import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryResponseDto;
import com.sparta.delivery.domain.delivery.dto.person.DeliveryPersonRequestDto;
import com.sparta.delivery.domain.delivery.model.DeliveryPerson;
import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import com.sparta.delivery.domain.delivery.model.enums.DeliveryPersonType;
import com.sparta.delivery.infrastructure.persistence.DeliveryRouteJpaRepository;
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

    private final DeliveryRouteJpaRepository deliveryRouteJpaRepository;

    @Transactional
    public DeliveryResponseDto assignDeliveryPerson(UUID deliveryId, DeliveryPersonRequestDto deliveryPersonRequestDto) {

        DeliveryRoute deliveryRoute = getDeliveryRouteEntity(deliveryId);
        DeliveryPerson deliveryPerson = getDeliveryPersonEntity(deliveryPersonRequestDto);

        if (deliveryPerson.getType() == DeliveryPersonType.HUB_MOVEMENT) {
            if (!isHubToHubTransfer(deliveryRoute)) {
                throw new ServiceException("허브 이동 담당자는 업체 배송을 할 수 없습니다.");
            }
        } else if (deliveryPerson.getType() == DeliveryPersonType.COMPANY_DELIVERY && isHubToHubTransfer(deliveryRoute)) {
            throw new ServiceException("업체 배송 담당자는 허브 간 이동을 할 수 없습니다.");
        }

        deliveryRoute.assignDeliveryPerson(deliveryPerson.getDeliveryPersonId(), deliveryPerson.getSlackId());
        deliveryRouteJpaRepository.save(deliveryRoute);

        log.info("배송 담당자가 할당되었습니다: {}", deliveryPerson.getDeliveryPersonId());
        return DeliveryResponseDto.from(deliveryRoute.getDelivery(), deliveryRoute);
    }

    private boolean isHubToHubTransfer(DeliveryRoute deliveryRoute) {
        return deliveryRoute.getRouteSegments().stream()
                .allMatch(segment -> segment.getHubId() != null && segment.getAddress() == null);
    }

    private DeliveryPerson getDeliveryPersonEntity(DeliveryPersonRequestDto deliveryPersonRequestDto) {
        // TODO: 실제 배송 담당자 정보를 조회하는 로직 추가
        return DeliveryPerson.create(
                deliveryPersonRequestDto.getUserId(),
                deliveryPersonRequestDto.getHubId(),
                deliveryPersonRequestDto.getDeliveryPersonSlackId(),
                deliveryPersonRequestDto.getDeliveryPersonType()
        );
    }

    private DeliveryRoute getDeliveryRouteEntity(UUID routeId) {
        return deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 DeliveryRoute를 찾을 수 없습니다: " + routeId));
    }
}
