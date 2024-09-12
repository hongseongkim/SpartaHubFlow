package com.sparta.route.domain.model.enums;

public enum DeliveryRouteStatus {
    WAITING_FOR_HUB_TRANSFER("허브 이동 대기중"),
    IN_HUB_TRANSFER("허브 이동중"),
    ARRIVED_AT_HUB("허브 도착"),
    PROCESSING_AT_HUB("허브 처리중"),
    DEPARTED_FROM_HUB("허브 출발"),
    IN_TRANSIT("배송중"),
    DELIVERED("배송 완료");

    private final String description;

    DeliveryRouteStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
