package com.sparta.hub.domain.route.model;

import com.sparta.hub.application.configuration.auditing.listener.SoftDeleteListener;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "p_hub_route")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class HubRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hub_route_id")
    private UUID hubRouteId;

    @Column(name = "origin_hub_id")
    private UUID originHubId;

    @Column(name = "destination_hub_id")
    private UUID destinationHubId;

    @ElementCollection
    @CollectionTable(name = "p_hub_route_segments", joinColumns = @JoinColumn(name = "hub_route_id"))
    @OrderColumn(name = "segment_order")
    private List<UUID> routeSegments;

    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Column(name = "estimated_distance")
    private Double estimatedDistance;

    @Column(name = "route_display_name")
    private String routeDisplayName;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_by")
    private String deletedBy;

    public static HubRoute create(UUID originHubId, UUID destinationHubId) {
        return new HubRoute(null, originHubId, destinationHubId, null, null, null, new ArrayList<>());
    }

    private HubRoute(UUID hubRouteId, UUID originHubId, UUID destinationHubId, Integer estimatedTime, Double estimatedDistance,String routeDisplayName, List<UUID> routeSegments) {
        this.hubRouteId = hubRouteId;
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
        this.estimatedTime = estimatedTime;
        this.estimatedDistance = estimatedDistance;
        this.routeDisplayName = routeDisplayName;
        this.routeSegments = routeSegments;
    }

    public void updateOriginAndDestination(UUID originHubId, UUID destinationHubId) {
        if (originHubId != null) {
            this.originHubId = originHubId;
        }
        if (destinationHubId != null) {
            this.destinationHubId = destinationHubId;
        }
    }

    public void updateHubRouteInfo (Integer estimatedTime, Double estimatedDistance,String routeDisplayName) {
        if (estimatedTime != null) {
            this.estimatedTime = estimatedTime;
        }
        if (estimatedDistance != null) {
            this.estimatedDistance = estimatedDistance;
        }
        if (routeDisplayName != null && !routeDisplayName.isEmpty()) {
            this.routeDisplayName = routeDisplayName;
        }
    }

    public void updateRouteSegments(List<UUID> routeSegments) {
        if (!routeSegments.isEmpty()) {
            this.routeSegments = routeSegments;
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
