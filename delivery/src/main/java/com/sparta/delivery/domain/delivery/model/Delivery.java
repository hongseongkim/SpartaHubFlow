package com.sparta.delivery.domain.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.delivery.domain.delivery.model.enums.DeliveryStatus;
import com.sparta.delivery.infrastructure.configuration.auditing.listener.SoftDeleteListener;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "p_deliveries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "order_id")
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private DeliveryStatus status = DeliveryStatus.READY_FOR_DELIVERY;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "receiver_slack_id")
    private String receiverSlackId;

    @OneToOne(mappedBy = "delivery", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private DeliveryRoute route;

    @OneToOne(mappedBy = "delivery", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private DeliveryPerson deliveryPerson;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "actual_time")
    private Integer actualTime;

    @Column(name = "delivery_start_time")
    private LocalDateTime deliveryStartTime;

    @Column(name = "delivery_end_time")
    private LocalDateTime deliveryEndTime;

    @JsonIgnore
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonIgnore
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @JsonIgnore
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @JsonIgnore
    @Column(name = "deleted_by")
    private String deletedBy;

    public static Delivery create(UUID orderId, String deliveryAddress, Long receiverId, String receiverSlackId) {
        return new Delivery(orderId, deliveryAddress, receiverId, receiverSlackId);
    }

    private Delivery(UUID orderId, String deliveryAddress, Long receiverId, String receiverSlackId) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.receiverId = receiverId;
        this.receiverSlackId = receiverSlackId;
    }

    public void updateDeliveryInfo(UUID orderId, String deliveryAddress, Long receiverId, String receiverSlackId) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.receiverId = receiverId;
        this.receiverSlackId = receiverSlackId;
    }

    public void updateStatus(DeliveryStatus newStatus) {
        this.status = newStatus;
    }

    public void updateRoute(DeliveryRoute route) {
        this.route = route;
        if (route != null && route.getDelivery() != this) {
            route.updateDelivery(this);
        }
    }

    public void updateDeliveryPerson(DeliveryPerson deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    // 배송 시작 시간 설정
    public void startDelivery() {
        this.deliveryStartTime = LocalDateTime.now();
    }

    // 배송 완료 시 실제 소요 시간을 계산하는 메소드
    public void completeDelivery() {
        this.deliveryEndTime = LocalDateTime.now();
        calculateActualTime();
    }

    // 실제 소요 시간을 계산하는 메소드
    private void calculateActualTime() {
        if (this.deliveryStartTime != null && this.deliveryEndTime != null) {
            this.actualTime = Math.toIntExact(java.time.Duration.between(deliveryStartTime, deliveryEndTime).toMinutes() + 60); // 분 단위로 시간 계산
        }
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

}
