package com.sparta.delivery.domain.delivery.service;

import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryRequestDto;
import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryResponseDto;
import com.sparta.delivery.domain.delivery.dto.slack.SlackMessageRequestDto;
import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.delivery.model.DeliveryPerson;
import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import com.sparta.delivery.domain.delivery.model.RouteSegment;
import com.sparta.delivery.domain.delivery.model.enums.DeliveryPersonType;
import com.sparta.delivery.infrastructure.client.SlackFeignClient;
import com.sparta.delivery.infrastructure.persistence.DeliveryJpaRepository;
import com.sparta.delivery.infrastructure.persistence.DeliveryPersonJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Random;
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
    private final DeliveryPersonJpaRepository deliveryPersonJpaRepository;
    private final DeliveryRouteService deliveryRouteService;
    private final SlackFeignClient slackFeignClient;

    @Transactional
    public DeliveryResponseDto createDelivery(DeliveryRequestDto deliveryRequestDto) {

        log.info("새로운 주문에 대한 배송 생성 중: {}", deliveryRequestDto.getOrderId());

        try {
            Delivery delivery = createAndSaveDelivery(deliveryRequestDto);
            DeliveryRoute route = deliveryRouteService.createAndSaveDeliveryRoute(deliveryRequestDto, delivery);

            delivery.updateRoute(route);
            delivery = deliveryJpaRepository.save(delivery);
            log.info("배송 경로가 성공정으로 생성되었습니다. ID: {}", delivery.getDeliveryId());

            DeliveryPerson deliveryPerson = assignDeliveryPerson(delivery);
            log.info("배송 담당자가 할당되었습니다: {}", deliveryPerson.getDeliveryPersonId());

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

    @Transactional
    public DeliveryPerson assignDeliveryPerson(Delivery delivery) {

        // TODO 실제 user와 연동
        DeliveryPerson deliveryPerson = DeliveryPerson
                .create(new Random().nextLong(), "lemingul7@gmail.com", DeliveryPersonType.COMPANY_DELIVERY, delivery);

        if (deliveryPerson.getType() == DeliveryPersonType.HUB_MOVEMENT && !isHubToHubTransfer(delivery)) {
            throw new ServiceException("허브 이동 담당자는 업체 배송을 할 수 없습니다.");
        } else if (deliveryPerson.getType() == DeliveryPersonType.COMPANY_DELIVERY && isHubToHubTransfer(delivery)) {
            throw new ServiceException("업체 배송 담당자는 허브 간 이동을 할 수 없습니다.");
        }

        delivery.updateDeliveryPerson(deliveryPerson);
        deliveryJpaRepository.save(delivery);

        deliveryPerson.updateDelivery(delivery);
        deliveryPersonJpaRepository.save(deliveryPerson);

        slackFeignClient.sendSlackMessage(createSlackMessage(deliveryPerson, delivery));

        return deliveryPerson;
    }

    private boolean isHubToHubTransfer(Delivery delivery) {
        return delivery.getRoute().getRouteSegments().stream()
                .allMatch(segment -> segment.getHubId() != null && segment.getAddress() == null);
    }

    private SlackMessageRequestDto createSlackMessage(DeliveryPerson deliveryPerson, Delivery delivery) {
        return new SlackMessageRequestDto(deliveryPerson.getSlackEmail(), createDeliverySummary(delivery));
    }

    private String createDeliverySummary(Delivery delivery) {
        StringBuilder summary = new StringBuilder();
        summary.append("배송 요약:\n");
        summary.append("배송 ID: ").append(delivery.getDeliveryId()).append("\n");
        summary.append("주문 ID: ").append(delivery.getOrderId()).append("\n");
        summary.append("배송 주소: ").append(delivery.getDeliveryAddress()).append("\n");
        summary.append("수령인 ID: ").append(delivery.getReceiverId()).append("\n");
        summary.append("배송 상태: ").append(delivery.getStatus()).append("\n");

        if (delivery.getRoute() != null) {
            summary.append("경로 정보:\n");
            for (RouteSegment segment : delivery.getRoute().getRouteSegments()) {
                summary.append("  - ");
                if (segment.getHubId() != null) {
                    summary.append("허브 ID: ").append(segment.getHubId());
                } else {
                    summary.append("주소: ").append(segment.getAddress());
                }
                summary.append("\n");
            }
        }

        return summary.toString();
    }

}
