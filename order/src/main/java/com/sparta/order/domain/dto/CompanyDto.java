package com.sparta.order.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public interface CompanyDto {


    @Data
    @Builder
    class Response {
        private final UUID companyId;
        private final String companyName;
        private final UUID hubId;
        private final String companyAddress;
        private final Long companyMangerId;

    }



    @Data
    @NoArgsConstructor(force = true)
    class Delete {
        private final Long userIdToDelete;
    }
}
