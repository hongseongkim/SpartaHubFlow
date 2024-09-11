package com.sparta.company.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public interface HubDto {

    @Data
    @NoArgsConstructor(force = true)
    class Response {

        private final UUID hubId;
        private final String name;
        private final String address;
        private final UUID companyId;

    }

}
