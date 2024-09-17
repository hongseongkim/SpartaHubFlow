package com.sparta.delivery.domain.delivery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.delivery.infrastructure.configuration.auditing.listener.SoftDeleteListener;
import jakarta.persistence.*;
import java.util.ArrayList;
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
    @JsonBackReference
    private Delivery delivery;

    @Column(name = "origin_hub_id")
    private UUID originHubId;

    @Column(name = "destination_hub_id")
    private UUID destinationHubId;

    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Column(name = "estimated_distance")
    private Double estimatedDistance;

    @OneToMany(mappedBy = "deliveryRoute", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<RouteSegment> routeSegments = new ArrayList<>();

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

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

    private DeliveryRoute(UUID hubRouteId, UUID originHubId, UUID destinationHubId) {
        this.hubRouteId = hubRouteId;
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
    }

    public static DeliveryRoute create(UUID hubRouteId, UUID originHubId, UUID destinationHubId,
                                       Integer estimatedTime, Double estimatedDistance, List<RouteSegment> routeSegments) {
        DeliveryRoute route = new DeliveryRoute(hubRouteId, originHubId, destinationHubId);
        route.estimatedTime = estimatedTime;
        route.estimatedDistance = estimatedDistance;
        route.routeSegments = routeSegments;
        return route;
    }

    public void updateDelivery(Delivery delivery) {
        this.delivery = delivery;
        if (delivery != null && delivery.getRoute() != this) {
            delivery.updateRoute(this);
        }
    }

    public void updateRouteInfo(Integer estimatedTime, Double estimatedDistance) {
        this.estimatedTime = estimatedTime;
        this.estimatedDistance = estimatedDistance;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
}
