package com.sparta.delivery.domain.delivery.controller;

import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryRequestDto;
import com.sparta.delivery.domain.delivery.dto.delivery.DeliveryResponseDto;
import com.sparta.delivery.domain.delivery.service.DeliveryService;
import com.sparta.delivery.domain.delivery.dto.route.DeliveryPersonRequestDto;
import com.sparta.delivery.domain.delivery.dto.route.DeliveryRouteDto;
import com.sparta.delivery.domain.delivery.dto.route.DeliveryRouteRequestDto;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "배송 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping
    public ResponseEntity<DeliveryResponseDto> createDelivery(
            @Parameter(description = "배송 생성 요청 데이터", required = true) @Valid @RequestBody DeliveryRequestDto deliveryRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createDelivery(deliveryRequestDto));
    }

    @Operation(summary = "배송 정보 수정", description = "특정 배송의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음")
    })
    @PatchMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponseDto> updateDelivery(
            @Parameter(description = "수정할 배송 ID", required = true) @PathVariable UUID deliveryId,
            @Parameter(description = "배송 수정 요청 데이터", required = true) @Valid @RequestBody DeliveryRequestDto deliveryRequestDto) {

        return ResponseEntity.ok(deliveryService.updateDelivery(deliveryId, deliveryRequestDto));
    }

    @Operation(summary = "배송 경로 수정", description = "특정 배송의 경로를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송 경로 수정 성공"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음")
    })
    @PatchMapping("/{deliveryId}/routes")
    public ResponseEntity<DeliveryRouteDto> updateDeliveryRoute(
            @Parameter(description = "배송 경로를 수정할 배송 ID", required = true) @PathVariable UUID deliveryId,
            @Parameter(description = "배송 경로 수정 요청 데이터", required = true) @Valid @RequestBody DeliveryRouteRequestDto requestDto) {
        return ResponseEntity.ok(deliveryService.updateDeliveryRoute(deliveryId, requestDto));
    }

    @Operation(summary = "배송 담당자 할당", description = "특정 배송에 담당자를 할당합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송 담당자 할당 성공"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음")
    })
    @PatchMapping("/{deliveryId}/assign")
    public ResponseEntity<DeliveryResponseDto> assignDeliveryPerson(
            @Parameter(description = "배송 담당자를 할당할 배송 ID", required = true) @PathVariable UUID deliveryId,
            @Parameter(description = "배송 담당자 할당 요청 데이터", required = true) @RequestBody DeliveryPersonRequestDto requestDto) {
        return ResponseEntity.ok(deliveryService.assignDeliveryPerson(deliveryId, requestDto));
    }

    @Operation(summary = "배송 삭제", description = "특정 배송을 소프트 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "배송 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음")
    })
    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<Void> deleteDelivery(
            @Parameter(description = "삭제할 배송 ID", required = true) @PathVariable UUID deliveryId) {
        deliveryService.deleteDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "배송 경로 삭제", description = "특정 배송 경로를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "배송 경로 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "배송 경로를 찾을 수 없음")
    })
    @DeleteMapping("/{deliveryId}/routes")
    public ResponseEntity<Void> deleteDeliveryRoute(
            @Parameter(description = "삭제할 배송 경로의 배송 ID", required = true) @PathVariable UUID deliveryId) {
        deliveryService.deleteDeliveryRoute(deliveryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "배송 조회", description = "특정 배송의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송 조회 성공"),
            @ApiResponse(responseCode = "404", description = "배송을 찾을 수 없음")
    })
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponseDto> getDelivery(
            @Parameter(description = "조회할 배송 ID", required = true) @PathVariable UUID deliveryId) {
        return ResponseEntity.ok(deliveryService.getDelivery(deliveryId));
    }

    @Operation(summary = "특정 배송 경로 조회", description = "특정 배송의 경로 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송 경로 조회 성공"),
            @ApiResponse(responseCode = "404", description = "배송 경로를 찾을 수 없음")
    })
    @GetMapping("/{deliveryId}/routes")
    public ResponseEntity<List<DeliveryRouteDto>> getRoutesForDelivery(
            @Parameter(description = "경로를 조회할 배송 ID", required = true) @PathVariable UUID deliveryId) {
        return ResponseEntity.ok(deliveryService.getRoutesForDelivery(deliveryId));
    }

    @Operation(summary = "전체 배송 목록 조회", description = "모든 배송 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDto>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

}
