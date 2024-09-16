package com.sparta.delivery.domain.delivery.service;

import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryRequestDto;
import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryResponseDto;
import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.delivery.model.enums.DeliveryStatus;
import com.sparta.delivery.domain.delivery.dto.route.DeliveryPersonRequestDto;
import com.sparta.delivery.domain.delivery.dto.route.DeliveryRouteDto;
import com.sparta.delivery.domain.delivery.dto.route.DeliveryRouteRequestDto;
import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import com.sparta.delivery.domain.delivery.dto.hub.HubRouteResponseDto;
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
    public DeliveryResponseDto createDelivery(DeliveryRequestDto deliveryRequestDto) {
        log.info("Creating new delivery for order: {}", deliveryRequestDto.getOrderId());

        try {
            Delivery delivery = createAndSaveDelivery(deliveryRequestDto);
            DeliveryRoute route = createAndSaveDeliveryRoute(deliveryRequestDto, delivery);

            delivery.setRoute(route);
            delivery = deliveryJpaRepository.save(delivery);

            log.info("Delivery created successfully with ID: {}", delivery.getDeliveryId());
            return DeliveryResponseDto.from(delivery, route);
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
            HubRouteResponseDto hubRouteDto = hubFeignClient.getRoute(originHubId, destinationHubId);

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
    public DeliveryResponseDto updateDelivery(UUID deliveryId, DeliveryRequestDto deliveryRequestDto) {
        Delivery delivery = getDeliveryEntity(deliveryId);

        delivery.updateDeliveryInfo(
                deliveryRequestDto.getOrderId(),
                deliveryRequestDto.getDeliveryAddress(),
                deliveryRequestDto.getReceiverId(),
                deliveryRequestDto.getReceiverSlackId());

        deliveryJpaRepository.save(delivery);

        log.info("Delivery updated successfully: {}", deliveryId);

        return DeliveryResponseDto.from(delivery, delivery.getRoute());
    }

    @Transactional
    public DeliveryRouteDto updateDeliveryRoute(UUID deliveryId, DeliveryRouteRequestDto requestDto) {

        Delivery delivery = getDeliveryEntity(deliveryId);
        DeliveryRoute deliveryRoute = getDeliveryRouteEntity(delivery.getRoute().getDeliveryRouteId());

        try {
            HubRouteResponseDto hubRouteDto =
                    hubFeignClient.getRoute(requestDto.getOriginHubId(), requestDto.getDestinationHubId());

            deliveryRoute.updateRoute(
                    hubRouteDto.getHubRouteId(),
                    hubRouteDto.getOriginHubId(),
                    hubRouteDto.getDestinationHubId()
            );

            deliveryRoute.updateRouteInfo(
                    hubRouteDto.getEstimatedTime(),
                    hubRouteDto.getEstimatedDistance(),
                    hubRouteDto.getRouteSegments()
            );

            deliveryRouteJpaRepository.save(deliveryRoute);
            log.info("DeliveryRoute updated successfully: {}", delivery.getRoute().getDeliveryRouteId());

            return DeliveryRouteDto.from(deliveryRoute);

        } catch (FeignException e) {
            log.error("Error fetching hub route information: {}", e.getMessage());
            throw new ServiceException("Failed to fetch hub route information", e);
        }
    }

    @Transactional
    public void deleteDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryEntity(deliveryId);
        delivery.softDelete();
        deliveryJpaRepository.save(delivery);
    }

    @Transactional
    public void deleteDeliveryRoute(UUID deliveryId) {
        DeliveryRoute route = getDeliveryEntity(deliveryId).getRoute();
        route.softDelete();
        deliveryRouteJpaRepository.save(route);
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDto getDelivery(UUID deliveryId) {

        return DeliveryResponseDto.from(getDeliveryEntity(deliveryId), getDeliveryEntity(deliveryId).getRoute());
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponseDto> getAllDeliveries() {

       return getAllDeliveryEntities()
               .stream()
               .map(delivery -> DeliveryResponseDto.from(delivery, delivery.getRoute()))
               .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeliveryRouteDto> getRoutesForDelivery(UUID deliveryId) {

        return getRoutesForDeliveryEntities(deliveryId)
                .stream()
                .map(DeliveryRouteDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Delivery getDeliveryEntity(UUID deliveryId) {
        return deliveryJpaRepository.findByDeliveryIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found with ID: " + deliveryId));
    }

    @Transactional(readOnly = true)
    public List<Delivery> getAllDeliveryEntities() {
        return deliveryJpaRepository.findAllByIsDeletedFalse();
    }

    @Transactional(readOnly = true)
    public DeliveryRoute getDeliveryRouteEntity(UUID routeId) {
        return deliveryRouteJpaRepository.findById(routeId)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryRoute not found with ID: " + routeId));
    }

    @Transactional(readOnly = true)
    public List<DeliveryRoute> getRoutesForDeliveryEntities(UUID deliveryId) {
        return deliveryRouteJpaRepository.findByDeliveryDeliveryIdAndIsDeletedFalse(deliveryId);
    }

    @Transactional
    public DeliveryResponseDto assignDeliveryPerson(UUID deliveryId, DeliveryPersonRequestDto deliveryPersonRequestDto) {

        Delivery delivery = getDeliveryEntity(deliveryId);
        DeliveryRoute deliveryRoute = getDeliveryRouteEntity(delivery.getRoute().getDeliveryRouteId());

        deliveryRoute.assignDeliveryPerson(deliveryPersonRequestDto.getDeliveryPersonId(), deliveryPersonRequestDto.getDeliveryPersonSlackId());
        deliveryRouteJpaRepository.save(deliveryRoute);

        delivery.updateStatus(DeliveryStatus.ASSIGN_DELIVERY_PERSON);

        return DeliveryResponseDto.from(delivery, deliveryRoute);
    }
}
