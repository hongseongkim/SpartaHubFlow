package com.sparta.delivery.domain.delivery.controller;

import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryRequestDto;
import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryResponseDto;
import com.sparta.delivery.domain.delivery.service.DeliveryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/deliveries")
@Tag(name = "Delivery Service", description = "배송 관리 API")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "배송 생성", description = "새로운 배송을 생성합니다.")
    @PostMapping
    public ResponseEntity<DeliveryResponseDto> createDelivery(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole,
            @Valid @RequestBody DeliveryRequestDto deliveryRequestDto) {

        // 마스터 권한 검증
        authorizationService.validateMasterRole(userRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createDelivery(deliveryRequestDto));
    }

    @Operation(summary = "배송 정보 수정", description = "특정 배송의 정보를 수정합니다.")
    @PatchMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponseDto> updateDelivery(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole,
            @Parameter(description = "수정할 배송 ID", required = true) @PathVariable UUID deliveryId,
            @Valid @RequestBody DeliveryRequestDto deliveryRequestDto) {

        // 마스터 또는 허브 관리자 권한 검증
        UUID hubId = deliveryRequestDto.getOriginHubId();
        UUID userHubId = getUserHubId();
        authorizationService.validateManagerOrMasterRole(userRole, hubId, userHubId);

        return ResponseEntity.ok(deliveryService.updateDelivery(deliveryId, deliveryRequestDto));
    }

    @Operation(summary = "배송 삭제", description = "특정 배송을 소프트 삭제합니다.")
    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<Void> deleteDelivery(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole,
            @Parameter(description = "삭제할 배송 ID", required = true) @PathVariable UUID deliveryId) {

        // 마스터 권한 검증
        authorizationService.validateMasterRole(userRole);

        deliveryService.deleteDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "배송 조회", description = "특정 배송의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송 조회 성공"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음")
    })
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponseDto> getDelivery(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole,
            @Parameter(description = "조회할 배송 ID", required = true) @PathVariable UUID deliveryId) {
        return ResponseEntity.ok(deliveryService.getDelivery(deliveryId));
    }

    @Operation(summary = "전체 배송 목록 조회", description = "모든 배송 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDto>> getAllDeliveries(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = true) String userRole) {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    private UUID getUserHubId() {
        // TODO 실제 사용자 정보를 통해 허브 ID를 가져오는 로직을 구현
        return UUID.randomUUID();
    }
}
