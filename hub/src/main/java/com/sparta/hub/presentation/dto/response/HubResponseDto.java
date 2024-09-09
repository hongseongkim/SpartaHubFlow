package com.sparta.hub.presentation.dto.response;

import com.sparta.hub.domain.model.Hub;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class HubResponseDto {

    private UUID id;
    private String name;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static HubResponseDto fromEntity(Hub hub) {
        return HubResponseDto.builder()
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
