package com.sparta.delivery.domain.delivery.dto;

import com.sparta.delivery.domain.delivery.model.enums.DeliveryStatus;
import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.route.domain.model.DeliveryRoute;
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

    private UUID deliveryRouteId;
    private UUID originHubId;
    private UUID destinationHubId;
    private Long deliveryPersonId;
    private String deliveryPersonSlackId;

    public static DeliveryDto from(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .deliveryRouteId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .deliveryAddress(delivery.getDeliveryAddress())
                .receiverId(delivery.getReceiverId())
                .receiverSlackId(delivery.getReceiverSlackId())
                .build();
    }

    public static DeliveryDto from(Delivery delivery, DeliveryRoute route) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .deliveryRouteId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .deliveryAddress(delivery.getDeliveryAddress())
                .originHubId(route.getOriginHubId())
                .destinationHubId(route.getDestinationHubId())
                .receiverId(delivery.getReceiverId())
                .receiverSlackId(delivery.getReceiverSlackId())
                .deliveryPersonId(route.getDeliveryPersonId())
                .deliveryPersonSlackId(route.getDeliveryPersonSlackId())
                .build();
    }
}
