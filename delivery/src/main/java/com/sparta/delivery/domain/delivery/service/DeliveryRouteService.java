package com.sparta.delivery.domain.delivery.service;

import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryRequestDto;
import com.sparta.delivery.domain.delivery.dto.hub.HubRouteResponseDto;
import com.sparta.delivery.domain.delivery.dto.route.DeliveryRouteDto;
import com.sparta.delivery.domain.delivery.dto.route.DeliveryRouteRequestDto;
import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import com.sparta.delivery.domain.delivery.model.RouteSegment;
import com.sparta.delivery.infrastructure.client.HubFeignClient;
import com.sparta.delivery.infrastructure.persistence.DeliveryRouteJpaRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
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
public class DeliveryRouteService {

    private final DeliveryRouteJpaRepository deliveryRouteJpaRepository;
    private final HubFeignClient hubFeignClient;

    @Transactional
    public DeliveryRoute createAndSaveDeliveryRoute(DeliveryRequestDto deliveryRequestDto, Delivery delivery) {

        UUID originHubId = deliveryRequestDto.getOriginHubId();
        UUID destinationHubId = deliveryRequestDto.getDestinationHubId();

        try {
            HubRouteResponseDto hubRouteDto = hubFeignClient.getRoute(originHubId, destinationHubId);

            DeliveryRoute route = DeliveryRoute.create(
                    hubRouteDto.getHubRouteId(),
                    hubRouteDto.getOriginHubId(),
                    hubRouteDto.getDestinationHubId(),
                    hubRouteDto.getEstimatedTime(),
                    hubRouteDto.getEstimatedDistance(),
                    hubRouteDto.getRouteSegments().stream().map(RouteSegment::new).collect(Collectors.toList())
            );

            route.updateDelivery(delivery);
            deliveryRouteJpaRepository.save(route);

            // 배송지 경로 추가
            RouteSegment deliverySegment = new RouteSegment(
                    deliveryRequestDto.getDeliveryAddress(),
                    deliveryRequestDto.getLatitude(),
                    deliveryRequestDto.getLongitude()
            );
            deliverySegment.updateDeliveryRoute(route);
            route.getRouteSegments().add(deliverySegment);

            DeliveryRoute savedRoute = deliveryRouteJpaRepository.save(route);

            log.info("배송 경로가 성공적으로 저장되었습니다. ID: {}", savedRoute.getDeliveryRouteId());
            return savedRoute;

        } catch (FeignException e) {
            log.error("허브 서비스 호출 중 오류 발생: {}", e.getMessage());
            throw new ServiceException("허브 경로 정보를 가져오는 데 실패했습니다.", e);
        } catch (Exception e) {
            log.error("배송 경로 저장 중 오류 발생: {}", e.getMessage());
            throw new ServiceException("배송 경로 저장에 실패했습니다.", e);
        }
    }

    @Transactional
    public DeliveryRouteDto updateDeliveryRoute(UUID deliveryId, DeliveryRouteRequestDto requestDto) {
        DeliveryRoute deliveryRoute = getDeliveryRouteEntity(deliveryId);
        deliveryRoute.updateRouteInfo(requestDto.getEstimatedTime(), requestDto.getEstimatedDistance());
        deliveryRouteJpaRepository.save(deliveryRoute);

        log.info("DeliveryRoute가 성공적으로 업데이트되었습니다: {}", deliveryRoute.getDeliveryRouteId());
        return DeliveryRouteDto.from(deliveryRoute);
    }

    @Transactional(readOnly = true)
    public DeliveryRoute getDeliveryRouteEntity(UUID routeId) {
        return deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 DeliveryRoute를 찾을 수 없습니다: " + routeId));
    }

    @Transactional
    public void deleteDeliveryRoute(UUID deliveryId) {
        DeliveryRoute route = getDeliveryRouteEntity(deliveryId);
        route.softDelete();
        deliveryRouteJpaRepository.save(route);
    }
}
