package com.sparta.hub.application.dto;

import com.sparta.hub.domain.model.Hub;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class HubResponseDTO {

    private UUID id;
    private String name;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static HubResponseDTO fromEntity(Hub hub) {
        return HubResponseDTO.builder()
                .id(hub.getHubId())
                .name(hub.getName())
                .address(hub.getAddress())
                .createdAt(hub.getCreatedAt())
                .updatedAt(hub.getUpdatedAt())
                .createdBy(hub.getCreatedBy())
                .updatedBy(hub.getUpdatedBy())
                .build();
    }
}
