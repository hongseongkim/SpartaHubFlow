package com.sparta.delivery.domain.delivery.model;

import com.sparta.delivery.infrastructure.configuration.auditing.listener.SoftDeleteListener;
import jakarta.persistence.*;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_delivery_routes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class DeliveryRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_route_id")
    private UUID deliveryRouteId;

    @Column(name = "hub_route_id")
    private UUID hubRouteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Column(name = "origin_hub_id")
    private UUID originHubId;

    @Column(name = "destination_hub_id")
    private UUID destinationHubId;

    @Column(name = "delivery_person_id")
    private Long deliveryPersonId;

    @Column(name = "delivery_person_slack_id")
    private String deliveryPersonSlackId;

    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Column(name = "estimated_distance")
    private Double estimatedDistance;

    @ElementCollection
    @CollectionTable(name = "p_delivery_route_segments", joinColumns = @JoinColumn(name = "delivery_route_id"))
    @Column(name = "hub_id")
    private List<UUID> routeSegments;

    @Column(name = "actual_time")
    private Integer actualTime;

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

    private DeliveryRoute(UUID hubRouteId, UUID originHubId, UUID destinationHubId) {
        this.hubRouteId = hubRouteId;
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
    }

    public static DeliveryRoute create(UUID hubRouteId, UUID originHubId, UUID destinationHubId,
                                       Integer estimatedTime, Double estimatedDistance, List<UUID> routeSegments) {
        DeliveryRoute route = new DeliveryRoute(hubRouteId, originHubId, destinationHubId);
        route.estimatedTime = estimatedTime;
        route.estimatedDistance = estimatedDistance;
        route.routeSegments = routeSegments;
        return route;
    }

    public void updateRoute(UUID hubRouteId, UUID originHubId, UUID destinationHubId) {
        this.hubRouteId = hubRouteId;
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
    }

    public void updateRouteInfo(Integer estimatedTime, Double estimatedDistance, List<UUID> routeSegments) {
        this.estimatedTime = estimatedTime;
        this.estimatedDistance = estimatedDistance;
        this.routeSegments = routeSegments;
    }

    public void updateActualTime(Integer actualTime) {
        this.actualTime = actualTime;
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        if (delivery != null && delivery.getRoute() != this) {
            delivery.setRoute(this);
        }
    }

    public void assignDeliveryPerson(Long deliveryPersonId, String deliveryPersonSlackId) {
        this.deliveryPersonId = deliveryPersonId;
        this.deliveryPersonSlackId = deliveryPersonSlackId;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
}
