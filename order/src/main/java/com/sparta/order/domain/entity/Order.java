package com.sparta.order.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_order")
public class Order extends Timestamped{

    @Id
    @UuidGenerator
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "order_status", nullable = false)
    private OrderStatusEnum orderStatus;


    public Order(UUID productId, Integer quantity, OrderStatusEnum orderStatus) {
            this.productId = productId;
            this.quantity = quantity;
            this.orderStatus = orderStatus;
    }

    public void modify(UUID productId, Integer quantity, OrderStatusEnum orderStatusEnum) {
        this.productId = productId;
        this.quantity = quantity;
        this.orderStatus = orderStatusEnum;
    }

}
