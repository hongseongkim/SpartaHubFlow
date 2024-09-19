package com.sparta.company.domain.dto;

import com.sparta.company.domain.entity.Company;
import com.sparta.company.domain.entity.CompanyTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public interface CompanyDto {
    @Data
    @Builder
    class Create {
        private final String companyName;
        private final UUID hubId;
        private final CompanyTypeEnum companyType;
        private final String companyAddress;
        private final Long companyMangerId;
    }

    @Data
    @Builder
    class Modify {
        private final String companyName;
        private final UUID hubId;
        private final CompanyTypeEnum companyType;
        private final String companyAddress;
        private final Long companyMangerId;
    }

    @Data
    @Builder
    class DeleteResponse {
        private final UUID companyId;
    }

    @Data
    @Builder
    class Response {
        private final UUID companyId;
        private final String companyName;
        private final UUID hubId;
        private final CompanyTypeEnum companyType;
        private final String companyAddress;
        private final Long companyMangerId;

        public static Response of(Company company) {
            return Response.builder()
                    .companyId(company.getCompanyId())
                    .companyName(company.getCompanyName())
                    .hubId(company.getHubId())
                    .companyType(company.getCompanyType())
                    .companyAddress(company.getCompanyAddress())
                    .companyMangerId(company.getCompanyMangerId())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor(force = true)
    class GetAllCompanysResponse {
        private final UUID companyId;
        private final String companyName;
        private final UUID hubId;
        private final CompanyTypeEnum companyType;
        private final String companyAddress;
        private final Long companyMangerId;

        public GetAllCompanysResponse(Company company) {
            this.companyId = company.getCompanyId();
            this.companyName = company.getCompanyName();
            this.hubId = company.getHubId();
            this.companyType = company.getCompanyType();
            this.companyAddress = company.getCompanyAddress();
            this.companyMangerId = company.getCompanyMangerId();
        }
    }

    @Data
    @NoArgsConstructor(force = true)
    class Delete {
        private final Long userIdToDelete;
    }
}
