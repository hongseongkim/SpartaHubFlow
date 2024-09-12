package com.sparta.delivery.domain.controller;

import com.sparta.delivery.domain.dto.DeliveryDto;
import com.sparta.delivery.domain.dto.DeliveryRequestDto;
import com.sparta.delivery.domain.service.DeliveryService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryDto> createDelivery(@Valid @RequestBody DeliveryRequestDto deliveryRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createDelivery(deliveryRequestDto.toDTO()));
    }

    @PatchMapping("/{deliveryId}")
    public ResponseEntity<DeliveryDto> updateDelivery(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody DeliveryRequestDto deliveryRequestDto) {
        return ResponseEntity.ok(deliveryService.updateDelivery(deliveryId, deliveryRequestDto.toDTO()));
    }

    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable UUID deliveryId) {
        deliveryService.deleteDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryDto> getDelivery(@PathVariable UUID deliveryId) {
        return ResponseEntity.ok(deliveryService.getDelivery(deliveryId));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryDto>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }
}
