package com.sparta.delivery.domain.dto;

import com.sparta.delivery.domain.model.Delivery;
import com.sparta.delivery.domain.model.enums.DeliveryStatus;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class DeliveryDto {

    private UUID deliveryId;
    private UUID orderId;
    private DeliveryStatus status;
    private UUID departureHubId;
    private UUID destinationHubId;
    private String deliveryAddress;
    private Double latitude;
    private Double longitude;
    private UUID receiverId;
    private String receiverSlackId;

    public static DeliveryDto create(UUID orderId, UUID departureHubId, UUID destinationHubId,
                                     String deliveryAddress, UUID receiverId) {
        return DeliveryDto.builder()
                .orderId(orderId)
                .departureHubId(departureHubId)
                .destinationHubId(destinationHubId)
                .deliveryAddress(deliveryAddress)
                .receiverId(receiverId)
                .status(DeliveryStatus.READY_FOR_DELIVERY)
                .build();
    }

    public static DeliveryDto from(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .departureHubId(delivery.getDepartureHubId())
                .destinationHubId(delivery.getDestinationHubId())
                .deliveryAddress(delivery.getDeliveryAddress())
                .latitude(delivery.getLatitude())
                .longitude(delivery.getLongitude())
                .receiverId(delivery.getReceiverId())
                .receiverSlackId(delivery.getReceiverSlackId())
                .build();
    }

    public static Delivery toEntity(DeliveryDto dto) {
        return Delivery.create(
                dto.getOrderId(),
                dto.getDepartureHubId(),
                dto.getDestinationHubId(),
                dto.getDeliveryAddress(),
                dto.getReceiverId()
        );
    }

    public static DeliveryDto updateWith(UUID deliveryId, DeliveryStatus status, DeliveryDto updateData) {
        return DeliveryDto.builder()
                .deliveryId(deliveryId)
                .orderId(updateData.getOrderId())
                .status(status)
                .departureHubId(updateData.getDepartureHubId())
                .destinationHubId(updateData.getDestinationHubId())
                .deliveryAddress(updateData.getDeliveryAddress())
                .receiverId(updateData.getReceiverId())
                .receiverSlackId(updateData.getReceiverSlackId())
                .build();
    }
}
