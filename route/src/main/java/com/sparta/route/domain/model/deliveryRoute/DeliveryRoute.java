package com.sparta.route.domain.model.deliveryRoute;

import com.sparta.route.domain.model.deliveryRoute.enums.DeliveryStatus;
import com.sparta.route.infrastructure.configuration.auditing.listener.SoftDeleteListener;
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
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "p_delivery_route")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class DeliveryRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_route_id")
    private UUID deliveryRouteId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "origin_hub_id")
    private UUID originHubId;

    @Column(name = "destination_hub_id")
    private UUID destinationHubId;

    @Column(name = "delivery_person_id")
    private Long deliveryPersonId;

    @Column(name = "estimated_distance")
    private Double estimatedDistance;

    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Column(name = "actual_distance")
    private Double actualDistance;

    @Column(name = "actual_time")
    private Integer actualTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private DeliveryStatus currentStatus;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

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

    public static DeliveryRoute create(UUID deliveryId, UUID originHubId, UUID destinationHubId) {
        return new DeliveryRoute(null, deliveryId, originHubId, destinationHubId);
    }

    private DeliveryRoute(UUID deliveryRouteId, UUID deliveryId, UUID originHubId, UUID destinationHubId) {
        this.deliveryRouteId = deliveryRouteId;
        this.deliveryId = deliveryId;
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
    }

    // 1. 배송 경로 정보 업데이트
    public void updateRouteInfo(UUID originHubId, UUID destinationHubId, Long deliveryPersonId) {
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
        this.deliveryPersonId = deliveryPersonId;
    }

    // 2. 배송 진행 상태 업데이트
    public void updateStatus(DeliveryStatus newStatus) {
        this.currentStatus = newStatus;
    }

    // 3. 예상 정보 업데이트
    public void updateEstimations(Double estimatedDistance, Integer estimatedTime) {
        this.estimatedDistance = estimatedDistance;
        this.estimatedTime = estimatedTime;
    }

    // 4. 실제 정보 업데이트
    public void updateActuals(Double actualDistance, Integer actualTime) {
        this.actualDistance = actualDistance;
        this.actualTime = actualTime;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
}
