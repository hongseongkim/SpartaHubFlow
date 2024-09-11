package com.sparta.order.domain.entity;

import lombok.ToString;

@ToString
public enum OrderStatusEnum {
    DELIVERING("delivering"),
    DELIVERED("delivered"),
    CANCELED("canceled");

    private final String status;

    OrderStatusEnum(String status) { this.status = status; }

    public String getStatus() { return this.status; }

}
