package com.sparta.hub.domain.hub.controller;

import com.sparta.hub.domain.hub.dtos.request.HubManagerRequest;
import com.sparta.hub.domain.hub.dtos.request.HubRequest;
import com.sparta.hub.domain.hub.dtos.response.HubResponseDto;
import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.domain.hub.service.HubServiceImpl;
import com.sparta.hub.application.security.AuthorizationService;
import com.sparta.hub.application.utils.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
@Tag(name = "Hub Service", description = "허브 관리 API")
public class HubController {

    private final HubServiceImpl hubServiceImpl;
    private final AuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "허브 생성", description = "새로운 허브를 생성합니다. 매니저 권한이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "허브 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "409", description = "중복된 허브 이름")
    })
    public ResponseEntity<HubResponseDto> createHub(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = false) String userRole,
            @Parameter(description = "허브 생성 요청 정보", required = true) @RequestBody @Valid HubRequest hubRequest) {

        authorizationService.validateManagerRole(userRole);

        Hub createdHub = hubServiceImpl.createHub(hubRequest.toDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(HubResponseDto.from(createdHub));
    }

    @PatchMapping("/{hubId}/manager")
    @Operation(summary = "허브 매니저 할당", description = "특정 허브에 매니저를 할당합니다. 마스터 권한이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매니저 할당 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "허브를 찾을 수 없음")
    })
    public ResponseEntity<HubResponseDto> assignHubManager(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = false) String userRole,
            @Parameter(description = "허브 ID", required = true) @PathVariable UUID hubId,
            @Parameter(description = "허브 매니저 할당 요청 정보", required = true) @RequestBody @Valid HubManagerRequest hubManagerRequest) {

        authorizationService.validateMasterRole(userRole);

        Hub updatedHub = hubServiceImpl.assignHubManager(hubId, hubManagerRequest.toDTO());

        return ResponseEntity.ok(HubResponseDto.from(updatedHub));
    }

    @PatchMapping("/{hubId}")
    @Operation(summary = "허브 수정", description = "허브 정보를 수정합니다. 매니저 권한이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "허브를 찾을 수 없음")
    })
    public ResponseEntity<HubResponseDto> updateHub(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = false) String userRole,
            @Parameter(description = "허브 ID", required = true) @PathVariable UUID hubId,
            @Parameter(description = "허브 수정 요청 정보", required = true) @RequestBody @Valid HubRequest hubRequest) {

        authorizationService.validateManagerRole(userRole);

        Hub updatedHub = hubServiceImpl.updateHub(hubId, hubRequest.toDTO());

        return ResponseEntity.ok(HubResponseDto.from(updatedHub));
    }

    @DeleteMapping("/{hubId}")
    @Operation(summary = "허브 삭제", description = "허브를 소프트 삭제합니다. 매니저 권한이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "허브 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "허브를 찾을 수 없음")
    })
    public ResponseEntity<Void> softDeleteHub(
            @Parameter(description = "사용자 역할", required = true) @RequestHeader(value = "User-Role", required = false) String userRole,
            @Parameter(description = "삭제할 허브 ID", required = true) @PathVariable UUID hubId) {

        authorizationService.validateManagerRole(userRole);

        hubServiceImpl.softDeleteHub(hubId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{hubId}")
    @Operation(summary = "허브 조회", description = "특정 ID의 허브를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 조회 성공"),
            @ApiResponse(responseCode = "404", description = "허브를 찾을 수 없음")
    })
    public ResponseEntity<HubResponseDto> getHubById(
            @Parameter(description = "조회할 허브 ID", required = true) @PathVariable UUID hubId) {

        Hub hub = hubServiceImpl.getHubById(hubId);

        return ResponseEntity.ok(HubResponseDto.from(hub));
    }

    @GetMapping
    @Operation(summary = "허브 목록 페이지 조회", description = "모든 허브 목록을 페이지로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 목록 조회 성공")
    })
    public ResponseEntity<Page<HubResponseDto>> getAllHubs(
            @Parameter(description = "페이지 정보 (예: page=0&size=20&sort=name,desc)") Pageable pageable) {

        pageable = PageableUtils.applyPageSizeLimit(pageable);
        pageable = PageableUtils.applyDefaultSortIfNecessary(pageable);

        Page<Hub> hubs = hubServiceImpl.getAllHubs(pageable);

        return ResponseEntity.ok(hubs.map(HubResponseDto::from));
    }

    @GetMapping("/list")
    @Operation(summary = "전체 허브 목록 조회", description = "페이지네이션 없이 모든 허브 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 허브 목록 조회 성공")
    })
    public ResponseEntity<List<HubResponseDto>> getAllHubsWithoutPagination() {

        List<Hub> hubs = hubServiceImpl.getAllHubsWithoutPagination();

        List<HubResponseDto> hubResponseDtos = hubs.stream()
                .map(HubResponseDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(hubResponseDtos);
    }

    @GetMapping("/search")
    @Operation(summary = "허브 이름 검색", description = "허브 이름을 통해 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "허브 검색 성공")
    })
    public ResponseEntity<Page<HubResponseDto>> searchHubsByName(
            @Parameter(description = "검색할 허브 이름", required = true) @RequestParam String name,
            @Parameter(description = "페이지 정보 (예: page=0&size=10&sort=createdAt,asc)") Pageable pageable) {

        pageable = PageableUtils.applyPageSizeLimit(pageable);
        pageable = PageableUtils.applyDefaultSortIfNecessary(pageable);

        Page<Hub> hubs = hubServiceImpl.searchHubsByName(name, pageable);

        return ResponseEntity.ok(hubs.map(HubResponseDto::from));
    }
}