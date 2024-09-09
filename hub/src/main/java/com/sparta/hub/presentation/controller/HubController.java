package com.sparta.hub.presentation.controller;

import com.sparta.hub.application.common.response.ResponseBody;
import com.sparta.hub.application.common.response.ResponseUtil;
import com.sparta.hub.application.dto.HubRequestDTO;
import com.sparta.hub.application.dto.HubResponseDTO;
import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.application.service.HubService;
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
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<ResponseBody<HubResponseDTO>> createHub(
            @RequestBody @Valid HubRequestDTO hubRequestDTO) {
        Hub createdHub = hubService.createHub(hubRequestDTO.getName(), hubRequestDTO.getAddress());
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(HubResponseDTO.fromEntity(createdHub)));
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<ResponseBody<HubResponseDTO>> getHubById(@PathVariable UUID hubId) {
        Hub hub = hubService.getHubById(hubId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(HubResponseDTO.fromEntity(hub)));
    }

    @GetMapping
    public ResponseEntity<ResponseBody<Page<HubResponseDTO>>> getAllHubs(Pageable pageable) {
        Page<Hub> hubs = hubService.getAllHubs(pageable);
        Page<HubResponseDTO> responseDTOs = hubs.map(HubResponseDTO::fromEntity);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(responseDTOs));
    }

    @PatchMapping("/{hubId}")
    public ResponseEntity<ResponseBody<HubResponseDTO>> updateHub(
            @PathVariable UUID hubId,
            @RequestBody @Valid HubRequestDTO hubRequestDTO) {
        Hub updatedHub = hubService.updateHub(hubId, hubRequestDTO.getName(), hubRequestDTO.getAddress());
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(HubResponseDTO.fromEntity(updatedHub)));
    }

    @DeleteMapping("/{hubId}")
    public ResponseEntity<ResponseBody<Void>> softDeleteHub(
            @PathVariable UUID hubId,
            @RequestParam String deletedBy) {
        hubService.softDeleteHub(hubId, deletedBy);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseBody<Page<HubResponseDTO>>> searchHubsByName(
            @RequestParam String name,
            Pageable pageable) {
        Page<Hub> hubs = hubService.searchHubsByName(name, pageable);
        Page<HubResponseDTO> responseDTOs = hubs.map(HubResponseDTO::fromEntity);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(responseDTOs));
    }
}
