package com.sparta.route.domain.dto.hub;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Builder(access = AccessLevel.PRIVATE)
public class HubDto {

    private UUID hubId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static HubDto from(HubResponseDto hubResponseDto) {
        return HubDto.builder()
                .hubId(hubResponseDto.getHubId())
                .name(hubResponseDto.getName())
                .address(hubResponseDto.getAddress())
                .latitude(hubResponseDto.getLatitude())
                .longitude(hubResponseDto.getLongitude())
                .isDeleted(hubResponseDto.isDeleted())
                .createdAt(hubResponseDto.getCreatedAt())
                .updatedAt(hubResponseDto.getUpdatedAt())
                .createdBy(hubResponseDto.getCreatedBy())
                .updatedBy(hubResponseDto.getUpdatedBy())
                .build();
    }

    public static Map<UUID, HubDto> mapFromResponses(List<HubResponseDto> responses) {
        return responses.stream()
                .map(HubDto::from)
                .collect(Collectors.toMap(HubDto::getHubId, Function.identity()));
    }

}
