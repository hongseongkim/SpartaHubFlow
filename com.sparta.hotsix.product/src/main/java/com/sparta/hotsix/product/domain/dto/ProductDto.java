package com.sparta.hotsix.product.domain.dto;

import com.sparta.hotsix.product.domain.entity.Product;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public interface ProductDto {

    @Data
    @Builder
    class Create {
        private final String productName;
        private final String description;
        private final UUID hubId;
        private final UUID companyId;
    }

    @Data
    @Builder
    class Modify {
        private final String productName;
        private final String description;
        private final UUID hubId;
        private final UUID companyId;
    }

    @Data
    @Builder
    class DeleteResponse {
        private final UUID productId;
    }

    @Data
    @Builder
    class Response {
        private final UUID productId;
        private final String productName;
        private final String description;
        private final UUID hubId;
        private final UUID companyId;

        public static Response of(Product product) {
            return Response.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .description(product.getDescription())
                    .hubId(product.getHubId())
                    .companyId(product.getCompanyId())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor(force = true)
    class GetAllProductsResponse{
        private final UUID productId;
        private final String productName;
        private final String description;
        private final UUID hubId;
        private final UUID companyId;

        public GetAllProductsResponse(Product product) {
            this.productId = product.getProductId();
            this.productName = product.getProductName();
            this.description = product.getDescription();
            this.hubId = product.getHubId();
            this.companyId = product.getCompanyId();
        }
    }

    @Data
    @NoArgsConstructor(force = true)
    class Delete {
        private final Long userIdToDelete;
    }
}
