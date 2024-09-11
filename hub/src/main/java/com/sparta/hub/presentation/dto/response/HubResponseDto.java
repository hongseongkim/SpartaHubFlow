package com.sparta.hub.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sparta.hub.domain.model.Hub;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HubResponseDto {

    private UUID id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static HubResponseDto fromEntity(Hub hub) {
        return HubResponseDto.builder()
                .id(hub.getHubId())
                .name(hub.getName())
                .address(hub.getAddress())
                .latitude(hub.getLatitude())
                .longitude(hub.getLongitude())
                .isDeleted(hub.isDeleted())
                .createdAt(hub.getCreatedAt())
                .updatedAt(hub.getUpdatedAt())
                .createdBy(hub.getCreatedBy())
                .updatedBy(hub.getUpdatedBy())
                .build();
    }
}
