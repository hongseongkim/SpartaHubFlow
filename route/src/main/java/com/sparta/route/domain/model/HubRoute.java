package com.sparta.route.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sparta.route.infrastructure.configuration.auditing.listener.SoftDeleteListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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

    @Column(name = "estimated_time")
    private Integer estimatedTime;

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
    private String createdBy = "ADMIN";

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_by")
    private String deletedBy;

    public static HubRoute create(UUID originHubId, UUID destinationHubId) {
        return new HubRoute(null, originHubId, destinationHubId);
    }

    private HubRoute(UUID hubRouteId, UUID originHubId, UUID destinationHubId) {
        this.hubRouteId = hubRouteId;
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
    }

    public void updateEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public void updateRouteDisplayName(String routeDisplayName) {
        this.routeDisplayName = routeDisplayName;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
}
