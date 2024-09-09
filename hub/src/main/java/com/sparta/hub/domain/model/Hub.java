package com.sparta.hub.domain.model;

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
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "p_hub")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hub_id", updatable = false, nullable = false)
    private UUID hubId;

    @Column(name = "hub_name", nullable = false)
    private String name;

    @Column(name = "hub_location", nullable = false)
    private String address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

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

    public static Hub create(String name, String address, String createdBy) {
        return new Hub(null, name, address, createdBy);
    }

    public static Hub create(String name, String address) {
        return new Hub(null, name, address);
    }

    private Hub(UUID hubId, String name, String address) {
        this.hubId = hubId;
        this.name = name;
        this.address = address;
    }

    private Hub(UUID hubId, String name, String address, String createdBy) {
        this.hubId = hubId;
        this.name = name;
        this.address = address;
        this.createdBy = createdBy;
    }

    public void updateAddress(String newAddress) {
        if (newAddress != null && !newAddress.isEmpty()) {
            this.address = newAddress;
        }
    }

    public void updateName(String newName) {
        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
        }
    }

    public void softDelete(String deletedBy) {
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }
}
