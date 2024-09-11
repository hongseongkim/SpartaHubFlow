package com.sparta.hotsix.product.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public interface CompanyDto {
    @Data
    @NoArgsConstructor(force = true)
    class Response {

        private final UUID companyId;
        private final String companyName;
        private final UUID hubId;
        private final Enum<?> companyType;
        private final String companyAddress;

    }
}
