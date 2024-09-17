package com.sparta.delivery.domain.delivery.controller;

import com.sparta.delivery.domain.delivery.service.DeliveryStatusService;
import com.sparta.delivery.infrastructure.security.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/deliveries")
@Tag(name = "Delivery Status Service", description = "배송 상태 관리 API")
public class DeliveryStatusController {

    private final DeliveryStatusService deliveryStatusService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "배송 시작", description = "특정 배송을 시작합니다.")
    @PatchMapping("/{deliveryId}/start")
    public ResponseEntity<Void> startDelivery(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole,
            @Parameter(description = "배송을 시작할 배송 ID", required = true) @PathVariable UUID deliveryId) {

        authorizationService.validateMasterRole(userRole);

        deliveryStatusService.startDelivery(deliveryId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "배송 완료", description = "특정 배송을 완료합니다.")
    @PatchMapping("/{deliveryId}/complete")
    public ResponseEntity<Void> completeDelivery(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole,
            @Parameter(description = "배송을 완료할 배송 ID", required = true) @PathVariable UUID deliveryId) {

        authorizationService.validateMasterRole(userRole);

        deliveryStatusService.completeDelivery(deliveryId);
        return ResponseEntity.ok().build();
    }
}

