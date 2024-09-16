package com.sparta.hub.domain.route.controller;

import com.sparta.hub.application.security.AuthorizationService;
import com.sparta.hub.application.utils.PageableUtils;
import com.sparta.hub.domain.route.dto.HubRoutePatchDto;
import com.sparta.hub.domain.route.dto.HubRouteRequestDto;
import com.sparta.hub.domain.route.dto.HubRouteResponseDto;
import com.sparta.hub.domain.route.model.HubRoute;
import com.sparta.hub.domain.route.service.HubRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/hubs/routes")
@Tag(name = "Hub Route", description = "Hub Route 관리 API")
public class HubRouteController {

    private final HubRouteService hubRouteService;
    private final AuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "새로운 허브 경로 생성", description = "출발지 허브와 목적지 허브 ID를 받아 새로운 허브 경로를 생성합니다. 마스터 권한이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "허브 경로 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "허브를 찾을 수 없음")
    })
    public ResponseEntity<HubRouteResponseDto> createHubRoute(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = false) String userRole,
            @Valid @RequestBody HubRouteRequestDto hubRouteRequestDto) {

        authorizationService.validateMasterRole(userRole);

        UUID originHubId = hubRouteRequestDto.getOriginHubId();
        UUID destinationHubId = hubRouteRequestDto.getDestinationHubId();

        HubRoute createdHubRoute = hubRouteService.createHubRoute(originHubId, destinationHubId);
        return ResponseEntity.status(HttpStatus.CREATED).body(HubRouteResponseDto.from(createdHubRoute));
    }

    @PatchMapping("/{hubRouteId}")
    @Operation(summary = "허브 경로 정보 수정", description = "기존 허브 경로의 정보를 수정합니다. 마스터 권한이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 경로 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "허브 경로를 찾을 수 없음")
    })
    public ResponseEntity<HubRouteResponseDto> updateHubRoute(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = false) String userRole,
            @Parameter(description = "수정할 허브 경로 ID") @PathVariable UUID hubRouteId,
            @Valid @RequestBody HubRoutePatchDto hubRoutePatchDto) {

        authorizationService.validateMasterRole(userRole);

        HubRoute updatedHubRoute = hubRouteService.updateHubRoute(hubRouteId, hubRoutePatchDto);
        return ResponseEntity.ok(HubRouteResponseDto.from(updatedHubRoute));
    }

    @DeleteMapping("/{hubRouteId}")
    @Operation(summary = "허브 경로 삭제", description = "지정된 ID의 허브 경로를 삭제합니다. 마스터 권한이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "허브 경로 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "허브 경로를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteHubRoute(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = false) String userRole,
            @Parameter(description = "삭제할 허브 경로 ID") @PathVariable UUID hubRouteId) {

        authorizationService.validateMasterRole(userRole);

        hubRouteService.deleteHubRoute(hubRouteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{hubRouteId}")
    @Operation(summary = "특정 허브 경로 조회", description = "지정된 ID의 허브 경로 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 경로 조회 성공"),
            @ApiResponse(responseCode = "404", description = "허브 경로를 찾을 수 없음")
    })
    public ResponseEntity<HubRouteResponseDto> getHubRoute(@Parameter(description = "조회할 허브 경로 ID") @PathVariable UUID hubRouteId) {

        HubRoute hubRoute = hubRouteService.getHubRoute(hubRouteId);
        return ResponseEntity.ok(HubRouteResponseDto.from(hubRoute));
    }

    @GetMapping("/{originHubId}/{destinationHubId}")
    @Operation(summary = "특정 허브 경로 조회", description = "출발 허브 ID와 도착 허브 ID로 허브 경로 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 경로 조회 성공"),
            @ApiResponse(responseCode = "404", description = "허브 경로를 찾을 수 없음")
    })
    public ResponseEntity<HubRouteResponseDto> getRoute(@PathVariable UUID originHubId, @PathVariable UUID destinationHubId) {

        HubRoute hubRoute = hubRouteService.getHubRouteByOriginAndDestination(originHubId, destinationHubId);

        return ResponseEntity.ok(HubRouteResponseDto.from(hubRoute));
    }

    @GetMapping("/origin/{originHubId}")
    @Operation(summary = "출발지 허브 기준 경로 조회", description = "특정 출발지 허브에서 시작하는 모든 허브 경로를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 경로 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "출발지 허브를 찾을 수 없음")
    })
    public ResponseEntity<List<HubRouteResponseDto>> getHubRoutesByOrigin(@Parameter(description = "출발지 허브 ID") @PathVariable UUID originHubId) {

        return ResponseEntity.ok(
                hubRouteService.getHubRoutesByOrigin(originHubId)
                        .stream()
                        .map(HubRouteResponseDto::from)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/destination/{destinationHubId}")
    @Operation(summary = "목적지 허브 기준 경로 조회", description = "특정 목적지 허브로 끝나는 모든 허브 경로를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 경로 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "목적지 허브를 찾을 수 없음")
    })
    public ResponseEntity<List<HubRouteResponseDto>> getHubRoutesByDestination(@Parameter(description = "목적지 허브 ID") @PathVariable UUID destinationHubId) {

        return ResponseEntity.ok(
                hubRouteService.getHubRoutesByDestination(destinationHubId)
                        .stream()
                        .map(HubRouteResponseDto::from)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping
    @Operation(summary = "허브 경로 목록 페이지 조회", description = "모든 허브 경로 목록을 페이지로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 경로 목록 조회 성공")
    })
    public ResponseEntity<Page<HubRouteResponseDto>> getAllHubRoutes(
            @Parameter(description = "페이지 정보 (예: page=0&size=10&sort=createdAt,asc)") Pageable pageable) {

        pageable = PageableUtils.applyPageSizeLimit(pageable);
        pageable = PageableUtils.applyDefaultSortIfNecessary(pageable);

        Page<HubRoute> hubRoutes = hubRouteService.getAllHubRoutes(pageable);

        return ResponseEntity.ok(hubRoutes.map(HubRouteResponseDto::from));
    }


}