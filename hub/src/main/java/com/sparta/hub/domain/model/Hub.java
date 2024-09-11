package com.sparta.hub.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sparta.hub.infrastructure.configuration.auditing.listener.SoftDeleteListener;
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
@Table(name = "p_hub")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hub_id", updatable = false, nullable = false)
    private UUID hubId;

    @Column(name = "hub_name", nullable = false)
    private String name;

    @Column(name = "hub_address", nullable = false)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "hub_manager_id")
    private Long hubManagerId;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
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

    public static Hub create(String name, String address, Double latitude, Double longitude) {
        return new Hub(null, name, address, latitude, longitude);
    }

    private Hub(UUID hubId, String name, String address, Double latitude, Double longitude) {
        this.hubId = hubId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateAddress(String newAddress) {
        this.address = newAddress;
    }

    public void updateCoordinates(Double newLatitude, Double newLongitude) {
        this.latitude = newLatitude;
        this.longitude = newLongitude;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

}
