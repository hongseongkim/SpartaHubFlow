package com.sparta.delivery.domain.delivery.controller;

import com.sparta.delivery.domain.delivery.dto.route.DeliveryRouteDto;
import com.sparta.delivery.domain.delivery.dto.route.DeliveryRouteRequestDto;
import com.sparta.delivery.domain.delivery.service.DeliveryRouteService;
import com.sparta.delivery.infrastructure.security.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/deliveries")
@Tag(name = "Delivery Route Service", description = "배송 경로 관리 API")
public class DeliveryRouteController {

    private final DeliveryRouteService deliveryRouteService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "배송 경로 수정", description = "특정 배송의 경로를 수정합니다.")
    @PatchMapping("/{deliveryId}/routes")
    public ResponseEntity<DeliveryRouteDto> updateDeliveryRoute(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole,
            @Parameter(description = "배송 경로를 수정할 배송 ID", required = true) @PathVariable UUID deliveryId,
            @Valid @RequestBody DeliveryRouteRequestDto requestDto) {

        // 마스터 또는 허브 관리자 권한 검증
        UUID hubId = getOriginHubId();
        UUID userHubId = getUserHubId();
        authorizationService.validateManagerOrMasterRole(userRole, hubId, userHubId);

        return ResponseEntity.ok(deliveryRouteService.updateDeliveryRoute(deliveryId, requestDto));
    }

    @Operation(summary = "배송 경로 삭제", description = "특정 배송 경로를 삭제합니다.")
    @DeleteMapping("/{deliveryId}/routes")
    public ResponseEntity<Void> deleteDeliveryRoute(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole,
            @Parameter(description = "삭제할 배송 경로의 배송 ID", required = true) @PathVariable UUID deliveryId) {

        // 마스터 권한 검증
        authorizationService.validateMasterRole(userRole);

        deliveryRouteService.deleteDeliveryRoute(deliveryId);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserHubId() {
        // TODO 실제 사용자 정보를 통해 허브 ID를 가져오는 로직을 구현
        return UUID.randomUUID();
    }

    private UUID getOriginHubId() {
        // TODO 실제 허브 ID를 가져오는 로직을 구현
        return UUID.randomUUID();
    }
}

