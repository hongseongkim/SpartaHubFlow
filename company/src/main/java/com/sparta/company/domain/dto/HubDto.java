package com.sparta.company.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public interface HubDto {

    @Data
    @NoArgsConstructor(force = true)
    class Response {

        private UUID id;
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private boolean isDeleted;
        private Long hubManagerId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
    }

}
