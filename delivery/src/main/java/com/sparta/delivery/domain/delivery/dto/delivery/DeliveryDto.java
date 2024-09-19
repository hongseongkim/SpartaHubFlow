package com.sparta.delivery.domain.delivery.dto.delivery;

import com.sparta.delivery.domain.delivery.model.enums.DeliveryStatus;
import com.sparta.delivery.domain.delivery.model.Delivery;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class DeliveryDto {

    private UUID deliveryId;
    private UUID orderId;
    private String deliveryAddress;
    private DeliveryStatus status;
    private Long receiverId;
    private String receiverSlackId;

    public static DeliveryDto from(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .deliveryAddress(delivery.getDeliveryAddress())
                .receiverId(delivery.getReceiverId())
                .receiverSlackId(delivery.getReceiverSlackId())
                .build();
    }
}
