package com.sparta.delivery.domain.delivery.service;

import com.sparta.delivery.domain.delivery.dto.DeliveryDto;
import com.sparta.delivery.domain.delivery.dto.DeliveryRequestDto;
import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.delivery.model.enums.DeliveryStatus;
import com.sparta.delivery.domain.route.domain.dto.DeliveryPersonRequestDto;
import com.sparta.delivery.domain.route.domain.dto.DeliveryRouteDto;
import com.sparta.delivery.domain.route.domain.dto.DeliveryRouteRequestDto;
import com.sparta.delivery.domain.route.domain.model.DeliveryRoute;
import com.sparta.delivery.domain.route.hub.HubRouteResponseDto;
import com.sparta.delivery.infrastructure.client.HubFeignClient;
import com.sparta.delivery.infrastructure.persistence.DeliveryJpaRepository;
import com.sparta.delivery.infrastructure.persistence.DeliveryRouteJpaRepository;
import feign.FeignException;
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
    private final DeliveryRouteJpaRepository deliveryRouteJpaRepository;
    private final HubFeignClient hubFeignClient;

    @Transactional
    public DeliveryDto createDelivery(DeliveryRequestDto deliveryRequestDto) {
        log.info("Creating new delivery for order: {}", deliveryRequestDto.getOrderId());

        try {
            Delivery delivery = createAndSaveDelivery(deliveryRequestDto);
            DeliveryRoute route = createAndSaveDeliveryRoute(deliveryRequestDto, delivery);

            delivery.setRoute(route);
            delivery = deliveryJpaRepository.save(delivery);

            log.info("Delivery created successfully with ID: {}", delivery.getDeliveryId());
            return DeliveryDto.from(delivery, route);
        } catch (Exception e) {
            log.error("Failed to create delivery: {}", e.getMessage());
            throw new ServiceException("Failed to create delivery", e);
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

    private DeliveryRoute createAndSaveDeliveryRoute(DeliveryRequestDto deliveryRequestDto, Delivery delivery) {
        UUID originHubId = deliveryRequestDto.getOriginHubId();
        UUID destinationHubId = deliveryRequestDto.getDestinationHubId();

        try {
            HubRouteResponseDto hubRouteDto = hubFeignClient.getHubRoutesByOrigin(originHubId)
                    .stream()
                    .filter(route -> route.getDestinationHubId().equals(destinationHubId))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Hub route not found"));

            DeliveryRoute route = DeliveryRoute.create(
                    hubRouteDto.getHubRouteId(),
                    originHubId,
                    destinationHubId,
                    hubRouteDto.getEstimatedTime(),
                    hubRouteDto.getEstimatedDistance(),
                    hubRouteDto.getRouteSegments()
            );
            route.setDelivery(delivery);

            DeliveryRoute savedRoute = deliveryRouteJpaRepository.save(route);
            log.info("DeliveryRoute saved successfully with ID: {}", savedRoute.getDeliveryRouteId());
            return savedRoute;
        } catch (FeignException e) {
            log.error("Error calling hub service: {}", e.getMessage());
            throw new ServiceException("Failed to get hub route information", e);
        } catch (Exception e) {
            log.error("Error saving delivery route: {}", e.getMessage());
            throw new ServiceException("Failed to save delivery route", e);
        }
    }

    @Transactional
    public DeliveryDto updateDelivery(UUID deliveryId, DeliveryRequestDto deliveryRequestDto) {
        Delivery delivery = deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        delivery.updateDeliveryInfo(
                deliveryRequestDto.getOrderId(),
                deliveryRequestDto.getDeliveryAddress(),
                deliveryRequestDto.getReceiverId(),
                deliveryRequestDto.getReceiverSlackId());

        deliveryJpaRepository.save(delivery);

        return DeliveryDto.from(delivery);
    }

    @Transactional
    public void updateDeliveryRoute(UUID routeId, DeliveryRouteRequestDto requestDto) {
        DeliveryRoute route = deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryRoute not found"));

        try {
            HubRouteResponseDto hubRouteDto = hubFeignClient.getHubRoute(route.getHubRouteId());

            route.updateRoute(
                    hubRouteDto.getHubRouteId(),
                    requestDto.getOriginHubId(),
                    requestDto.getDestinationHubId(),
                    hubRouteDto.getEstimatedTime(),
                    hubRouteDto.getEstimatedDistance(),
                    hubRouteDto.getRouteSegments()
            );

            deliveryRouteJpaRepository.save(route);
            log.info("DeliveryRoute updated successfully: {}", routeId);
        } catch (FeignException e) {
            log.error("Error fetching hub route information: {}", e.getMessage());
            throw new ServiceException("Failed to fetch hub route information", e);
        }
    }

    @Transactional
    public void deleteDelivery(UUID deliveryId) {
        Delivery delivery = deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found with ID: " + deliveryId));
        delivery.softDelete();
        deliveryJpaRepository.save(delivery);
    }

    @Transactional
    public void deleteDeliveryRoute(UUID routeId) {
        DeliveryRoute route = deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryRoute not found"));
        route.softDelete();
        deliveryRouteJpaRepository.save(route);
    }

    @Transactional(readOnly = true)
    public DeliveryDto getDelivery(UUID deliveryId) {
        return deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId)
                .map(DeliveryDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found with ID: " + deliveryId));
    }

    @Transactional(readOnly = true)
    public List<DeliveryDto> getAllDeliveries() {
        return deliveryJpaRepository.findAllByIsDeletedFalse().stream()
                .map(DeliveryDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeliveryRouteDto> getRoutesForDelivery(UUID deliveryId) {
        List<DeliveryRoute> routes = deliveryRouteJpaRepository.findByDeliveryDeliveryIdAndIsDeletedFalse(deliveryId);
        return routes.stream()
                .map(DeliveryRouteDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryDto assignDeliveryPerson(UUID deliveryId, UUID routeId, DeliveryPersonRequestDto deliveryPersonRequestDto) {

        DeliveryRoute route = deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryRoute not found"));

        route.assignDeliveryPerson(deliveryPersonRequestDto.getDeliveryPersonId(), deliveryPersonRequestDto.getDeliveryPersonSlackId());
        deliveryRouteJpaRepository.save(route);

        Delivery delivery = updateDeliveryStatus(deliveryId, DeliveryStatus.ASSIGN_DELIVERY_PERSON);

        return DeliveryDto.from(delivery, route);
    }

    @Transactional
    public Delivery updateDeliveryStatus(UUID deliveryId, DeliveryStatus deliveryStatus) {

        Delivery delivery = deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        delivery.updateStatus(deliveryStatus);
        return deliveryJpaRepository.save(delivery);
    }
}
