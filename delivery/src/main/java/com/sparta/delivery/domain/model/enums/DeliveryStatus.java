package com.sparta.delivery.domain.model.enums;

public enum DeliveryStatus {
    READY_FOR_DELIVERY("배송 준비중"),
    WAITING_FOR_HUB_TRANSFER("허브 이동 대기중"),
    IN_HUB_TRANSFER("허브 이동중"),

    ARRIVED_AT_HUB("허브 도착"),
    PROCESSING_AT_HUB("허브 처리중"),
    DEPARTED_FROM_HUB("허브 출발"),

    IN_TRANSIT("배송중"),
    DELIVERED("배송 완료"),

    PICKED_UP("물품 픽업됨"),
    ON_HOLD("배송 보류"),
    CANCELED("배송 취소됨");

    private final String description;

    DeliveryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
