package com.sparta.map.presentation.controller;

import com.sparta.map.application.service.MapApplicationService;
import com.sparta.map.presentation.dto.request.DirectionsRequest;
import com.sparta.map.presentation.dto.response.DirectionsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/maps")
@RequiredArgsConstructor
@Tag(name = "Map API", description = "지도 및 경로 관련 API")
public class MapController {

    private final MapApplicationService mapApplicationService;

    // 주소를 받아 좌표 정보를 반환하는 메서드
    @GetMapping("/coordinates")
    @Operation(summary = "좌표 요청", description = "주소를 입력하면 해당 주소의 좌표(위도, 경도)를 반환합니다.")
    public ResponseEntity<Map<String, Double>> getCoordinates(@RequestParam String address) {
        try {
            Map<String, Double> coordinates = mapApplicationService.getCoordinates(address);
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            log.error("Error occurred while processing coordinates request: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    // 여러 주소의 좌표 요청
    @PostMapping("/coordinates/batch")
    @Operation(summary = "여러 주소의 좌표 요청", description = "여러 주소를 입력받아 해당 주소들의 좌표(위도, 경도)를 반환합니다.")
    public ResponseEntity<Map<String, Map<String, Double>>> getMultipleCoordinates(@RequestBody List<String> addresses) {
        try {
            Map<String, Map<String, Double>> coordinates = mapApplicationService.getMultipleCoordinates(addresses);
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            log.error("Error occurred while processing multiple coordinates request: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    // 최적 경로 요청 (경유지 추가)
    @PostMapping("/directions/optimized")
    @Operation(summary = "최적 경로 요청", description = "출발지, 목적지, 경유지를 입력받아 최적 경로를 반환합니다.")
    public ResponseEntity<DirectionsResponse> getOptimizedDirections(@RequestBody DirectionsRequest request) {
        try {
            DirectionsResponse response = mapApplicationService.getOptimizedDirections(
                    request.getOrigin(), request.getDestination(), request.getWaypoints(), request.getMode());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while processing optimized directions request: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
}