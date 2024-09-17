package com.sparta.delivery.domain.delivery.controller;

import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryResponseDto;
import com.sparta.delivery.domain.delivery.dto.person.DeliveryPersonRequestDto;
import com.sparta.delivery.domain.delivery.service.DeliveryPersonService;
import com.sparta.delivery.infrastructure.security.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/deliveries")
@Tag(name = "Delivery Person Service", description = "배송 담당자 할당 API")
public class DeliveryPersonController {

    private final DeliveryPersonService deliveryPersonService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "배송 담당자 할당", description = "특정 배송에 담당자를 할당합니다.")
    @PatchMapping("/{deliveryId}/assign")
    public ResponseEntity<DeliveryResponseDto> assignDeliveryPerson(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole,
            @Parameter(description = "배송 담당자를 할당할 배송 ID", required = true) @PathVariable UUID deliveryId,
            @Valid @RequestBody DeliveryPersonRequestDto requestDto) {

        authorizationService.validateMasterRole(userRole);

        return ResponseEntity.ok(deliveryPersonService.assignDeliveryPerson(deliveryId, requestDto));
    }
}

