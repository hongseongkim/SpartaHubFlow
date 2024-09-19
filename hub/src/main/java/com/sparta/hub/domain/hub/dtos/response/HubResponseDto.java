package com.sparta.hub.domain.hub.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sparta.hub.domain.hub.model.Hub;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HubResponseDto {

    private UUID hubId;
    private String name;
    private String address;
    private Long hubManagerId;
    private String hubManagerSlackId;
    private Double latitude;
    private Double longitude;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static HubResponseDto from(Hub hub) {
        return HubResponseDto.builder()
                .hubId(hub.getHubId())
                .name(hub.getName())
                .address(hub.getAddress())
                .hubManagerId(hub.getHubManagerId())
                .hubManagerSlackId(hub.getHubManagerSlackId())
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
