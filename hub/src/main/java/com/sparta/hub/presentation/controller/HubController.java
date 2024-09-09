package com.sparta.hub.presentation.controller;

import com.sparta.hub.presentation.dto.response.common.ResponseBody;
import com.sparta.hub.presentation.dto.response.common.ResponseUtil;
import com.sparta.hub.presentation.dto.response.HubResponseDto;
import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.application.service.HubService;
import com.sparta.hub.presentation.dto.request.HubRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    @PostMapping
    public ResponseEntity<ResponseBody<HubResponseDto>> createHub(
            @RequestBody @Valid HubRequest hubRequest) {
        Hub createdHub = hubService.createHub(hubRequest.toDTO());
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(HubResponseDto.fromEntity(createdHub)));
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<ResponseBody<HubResponseDto>> getHubById(@PathVariable UUID hubId) {
        Hub hub = hubService.getHubById(hubId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(HubResponseDto.fromEntity(hub)));
    }

    @GetMapping
    public ResponseEntity<ResponseBody<Page<HubResponseDto>>> getAllHubs(Pageable pageable) {
        Page<Hub> hubs = hubService.getAllHubs(pageable);
        Page<HubResponseDto> responseDTOs = hubs.map(HubResponseDto::fromEntity);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(responseDTOs));
    }

    @PatchMapping("/{hubId}")
    public ResponseEntity<ResponseBody<HubResponseDto>> updateHub(
            @PathVariable UUID hubId,
            @RequestBody @Valid HubRequest hubRequest) {
        Hub updatedHub = hubService.updateHub(hubId, hubRequest.toDTO());
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(HubResponseDto.fromEntity(updatedHub)));
    }

    @DeleteMapping("/{hubId}")
    public ResponseEntity<ResponseBody<Void>> softDeleteHub(
            @PathVariable UUID hubId,
            @RequestParam String deletedBy) {
        hubService.softDeleteHub(hubId, deletedBy);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseBody<Page<HubResponseDto>>> searchHubsByName(
            @RequestParam String name,
            Pageable pageable) {
        Page<Hub> hubs = hubService.searchHubsByName(name, pageable);
        Page<HubResponseDto> responseDTOs = hubs.map(HubResponseDto::fromEntity);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(responseDTOs));
    }
}
