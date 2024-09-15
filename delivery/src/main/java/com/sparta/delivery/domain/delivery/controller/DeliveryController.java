package com.sparta.delivery.domain.delivery.controller;

import com.sparta.delivery.domain.delivery.dto.DeliveryDto;
import com.sparta.delivery.domain.delivery.dto.DeliveryRequestDto;
import com.sparta.delivery.domain.delivery.service.DeliveryService;
import com.sparta.delivery.domain.route.domain.dto.DeliveryPersonRequestDto;
import com.sparta.delivery.domain.route.domain.dto.DeliveryRouteDto;
import com.sparta.delivery.domain.route.domain.dto.DeliveryRouteRequestDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryDto> createDelivery(@Valid @RequestBody DeliveryRequestDto deliveryRequestDto) {
        DeliveryDto createdDelivery = deliveryService.createDelivery(deliveryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDelivery);
    }

    @PatchMapping("/{deliveryId}")
    public ResponseEntity<DeliveryDto> updateDelivery(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody DeliveryRequestDto deliveryRequestDto) {
        return ResponseEntity.ok(deliveryService.updateDelivery(deliveryId, deliveryRequestDto));
    }

    @PatchMapping("/{deliveryId}/routes/{routeId}")
    public ResponseEntity<Void> updateDeliveryRoute(
            @PathVariable UUID deliveryId,
            @PathVariable UUID routeId,
            @Valid @RequestBody DeliveryRouteRequestDto requestDto) {
        deliveryService.updateDeliveryRoute(routeId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{deliveryId}/routes/{routeId}/assign")
    public ResponseEntity<Void> assignDeliveryPerson(
            @PathVariable UUID deliveryId,
            @PathVariable UUID routeId,
            @RequestBody DeliveryPersonRequestDto requestDto) {
        deliveryService.assignDeliveryPerson(deliveryId, routeId, requestDto);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable UUID deliveryId) {
        deliveryService.deleteDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{deliveryId}/routes/{routeId}")
    public ResponseEntity<Void> deleteDeliveryRoute(
            @PathVariable UUID deliveryId,
            @PathVariable UUID routeId) {
        deliveryService.deleteDeliveryRoute(routeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryDto> getDelivery(@PathVariable UUID deliveryId) {
        DeliveryDto delivery = deliveryService.getDelivery(deliveryId);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping
    public ResponseEntity<List<DeliveryDto>> getAllDeliveries() {
        List<DeliveryDto> deliveries = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/{deliveryId}/routes")
    public ResponseEntity<List<DeliveryRouteDto>> getRoutesForDelivery(@PathVariable UUID deliveryId) {
        List<DeliveryRouteDto> routes = deliveryService.getRoutesForDelivery(deliveryId);
        return ResponseEntity.ok(routes);
    }

}
