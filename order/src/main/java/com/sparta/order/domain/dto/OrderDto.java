package com.sparta.order.domain.dto;

import com.sparta.order.domain.entity.Order;
import com.sparta.order.domain.entity.OrderStatusEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public interface OrderDto {

    @Data
    @Builder
    class Create {
        private final UUID productId;
        private final Integer quantity;
        private final OrderStatusEnum orderStatus;
        private final UUID departureCompanyId;
        private final UUID destinationCompanyId;
        private final Long receiverId;
        private final String deliveryAddress;
    }

    @Data
    @Builder
    class Modify {
        private final UUID productId;
        private final Integer quantity;
        private final OrderStatusEnum orderStatus;
    }

    @Data
    @Builder
    class DeleteResponse {
        private final UUID orderId;
    }

    @Data
    @Builder
    class Response {
        private final UUID orderId;
        private final UUID productId;
        private final Integer quantity;
        private final OrderStatusEnum orderStatus;

        public static Response of(Order order) {
            return Response.builder()
                    .orderId(order.getOrderId())
                    .productId(order.getProductId())
                    .quantity(order.getQuantity())
                    .orderStatus(order.getOrderStatus())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor(force = true)
    class GetAllOrdersResponse{
        private final UUID orderId;
        private final UUID productId;
        private final Integer quantity;
        private final OrderStatusEnum orderStatus;

        public GetAllOrdersResponse(Order order) {
            this.orderId = order.getOrderId();
            this.productId = order.getProductId();
            this.quantity = order.getQuantity();
            this.orderStatus = order.getOrderStatus();
        }
    }

    @Data
    @NoArgsConstructor(force = true)
    class Delete {
        private final Long userIdToDelete;
    }

}
