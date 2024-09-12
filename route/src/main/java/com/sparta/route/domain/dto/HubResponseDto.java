package com.sparta.route.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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

    @JsonCreator
    public HubResponseDto(@JsonProperty("id") UUID id,
                          @JsonProperty("name") String name,
                          @JsonProperty("address") String address,
                          @JsonProperty("latitude") double latitude,
                          @JsonProperty("longitude") double longitude,
                          @JsonProperty("createdAt") LocalDateTime createdAt,
                          @JsonProperty("updatedAt") LocalDateTime updatedAt,
                          @JsonProperty("createdBy") String createdBy,
                          @JsonProperty("updatedBy") String updatedBy,
                          @JsonProperty("isDeleted") boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.isDeleted = isDeleted;
    }

}
