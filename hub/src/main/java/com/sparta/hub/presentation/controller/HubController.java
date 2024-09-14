package com.sparta.hub.presentation.controller;

import com.sparta.hub.application.service.AuthorizationService;
import com.sparta.hub.presentation.dto.request.HubManagerRequest;
import com.sparta.hub.presentation.dto.response.HubResponseDto;
import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.domain.service.HubServiceImpl;
import com.sparta.hub.presentation.dto.request.HubRequest;
import com.sparta.hub.presentation.utils.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
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
public class HubController {

    private final HubServiceImpl hubServiceImpl;
    private final AuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "허브 생성", description = "새로운 허브를 생성합니다.")
    public ResponseEntity<HubResponseDto> createHub(
            @RequestHeader(value = "User-Role", required = false) String userRole,
            @RequestBody @Valid HubRequest hubRequest) {

        authorizationService.validateManagerRole(userRole);

        Hub createdHub = hubServiceImpl.createHub(hubRequest.toDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(HubResponseDto.fromEntity(createdHub));
    }

    @PatchMapping("/{hubId}/manager")
    @Operation(summary = "허브 매니저 할당", description = "허브에 매니저를 할당합니다.")
    public ResponseEntity<HubResponseDto> assignHubManager(
            @RequestHeader(value = "User-Role", required = false) String userRole,
            @PathVariable UUID hubId,
            @RequestBody @Valid HubManagerRequest hubManagerRequest) {

        authorizationService.validateMasterRole(userRole);

        Hub updatedHub = hubServiceImpl.assignHubManager(hubId, hubManagerRequest.toDTO());

        return ResponseEntity.ok(HubResponseDto.fromEntity(updatedHub));
    }

    @PatchMapping("/{hubId}")
    @Operation(summary = "허브 수정", description = "ID를 통해 허브 정보를 수정합니다.")
    public ResponseEntity<HubResponseDto> updateHub(
            @RequestHeader(value = "User-Role", required = false) String userRole,
            @PathVariable UUID hubId,
            @RequestBody @Valid HubRequest hubRequest) {

        authorizationService.validateManagerRole(userRole);

        Hub updatedHub = hubServiceImpl.updateHub(hubId, hubRequest.toDTO());

        return ResponseEntity.ok(HubResponseDto.fromEntity(updatedHub));
    }

    @DeleteMapping("/{hubId}")
    @Operation(summary = "허브 삭제", description = "ID를 통해 허브를 소프트 삭제합니다.")
    public ResponseEntity<Void> softDeleteHub(
            @RequestHeader(value = "User-Role", required = false) String userRole,
            @PathVariable UUID hubId) {

        authorizationService.validateManagerRole(userRole);

        hubServiceImpl.softDeleteHub(hubId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{hubId}")
    @Operation(summary = "허브 조회", description = "ID를 통해 허브를 조회합니다.")
    public ResponseEntity<HubResponseDto> getHubById(@PathVariable UUID hubId) {

        Hub hub = hubServiceImpl.getHubById(hubId);

        return ResponseEntity.ok(HubResponseDto.fromEntity(hub));
    }

    @GetMapping
    @Operation(summary = "허브 목록 조회", description = "모든 허브 목록을 조회합니다.")
    public ResponseEntity<Page<HubResponseDto>> getAllHubs(Pageable pageable) {

        pageable = PageableUtils.applyPageSizeLimit(pageable);
        pageable = PageableUtils.applyDefaultSortIfNecessary(pageable);

        Page<Hub> hubs = hubServiceImpl.getAllHubs(pageable);

        return ResponseEntity.ok(hubs.map(HubResponseDto::fromEntity));
    }

    @GetMapping("/full-list")
    @Operation(summary = "전체 허브 목록 조회", description = "페이지네이션 없이 모든 허브 목록을 조회합니다.")
    public ResponseEntity<List<HubResponseDto>> getAllHubsWithoutPagination() {

        List<Hub> hubs = hubServiceImpl.getAllHubsWithoutPagination();
        List<HubResponseDto> hubResponseDtos = hubs.stream()
                .map(HubResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(hubResponseDtos);
    }


    @GetMapping("/search")
    @Operation(summary = "허브 이름 검색", description = "허브 이름을 통해 검색합니다.")
    public ResponseEntity<Page<HubResponseDto>> searchHubsByName(
            @RequestParam String name,
            Pageable pageable) {

        pageable = PageableUtils.applyPageSizeLimit(pageable);
        pageable = PageableUtils.applyDefaultSortIfNecessary(pageable);

        Page<Hub> hubs = hubServiceImpl.searchHubsByName(name, pageable);

        return ResponseEntity.ok(hubs.map(HubResponseDto::fromEntity));
    }



}
