package com.sparta.delivery.domain.model;

import com.sparta.delivery.domain.model.enums.DeliveryStatus;
import com.sparta.delivery.infrastructure.configuration.auditing.listener.SoftDeleteListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "departure_hub_id")
    private UUID departureHubId;

    @Column(name = "destination_hub_id")
    private UUID destinationHubId;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "receiver_id")
    private UUID receiverId;

    @Column(name = "receiver_slack_id")
    private String receiverSlackId;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public static Delivery create(UUID orderId,
                                  UUID departureHubId, UUID destinationHubId,
                                  String deliveryAddress, UUID receiverId) {
        return new Delivery(null, orderId, departureHubId, destinationHubId, deliveryAddress, receiverId);
    }

    private Delivery(UUID deliveryId, UUID orderId,
                     UUID departureHubId, UUID destinationHubId,
                     String deliveryAddress, UUID receiverId) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.departureHubId = departureHubId;
        this.destinationHubId = destinationHubId;
        this.deliveryAddress = deliveryAddress;
        this.receiverId = receiverId;
    }

    public void updateStatus(DeliveryStatus newStatus) {
        this.status = newStatus;
    }

    public void updateDelivery(UUID departureHubId, UUID destinationHubId, String deliveryAddress,
                               UUID receiverId, String receiverSlackId) {
        this.departureHubId = departureHubId;
        this.destinationHubId = destinationHubId;
        this.deliveryAddress = deliveryAddress;
        this.receiverId = receiverId;
        this.receiverSlackId = receiverSlackId;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

}
