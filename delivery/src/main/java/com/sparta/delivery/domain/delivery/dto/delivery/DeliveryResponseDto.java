package com.sparta.delivery.domain.delivery.dto.delivery;

import com.sparta.delivery.domain.delivery.model.Delivery;
import com.sparta.delivery.domain.delivery.model.enums.DeliveryStatus;
import com.sparta.delivery.domain.delivery.model.DeliveryRoute;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class DeliveryResponseDto {
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
    private Integer estimatedTime;
    private Integer actualTime;
    private Double estimatedDistance;
    private List<UUID> routeSegments;

    public static DeliveryResponseDto from(Delivery delivery, DeliveryRoute route) {
        return DeliveryResponseDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .deliveryRouteId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .deliveryAddress(delivery.getDeliveryAddress())
                .receiverId(delivery.getReceiverId())
                .receiverSlackId(delivery.getReceiverSlackId())

                .originHubId(route.getOriginHubId())
                .destinationHubId(route.getDestinationHubId())
                .estimatedDistance(route.getEstimatedDistance())
                .estimatedTime(route.getEstimatedTime())
                .routeSegments(route.getRouteSegments())
                .deliveryPersonId(route.getDeliveryPersonId())
                .deliveryPersonSlackId(route.getDeliveryPersonSlackId())
                .build();
    }
}
