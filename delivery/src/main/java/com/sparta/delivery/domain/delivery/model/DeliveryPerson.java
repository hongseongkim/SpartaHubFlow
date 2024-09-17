package com.sparta.delivery.domain.delivery.model;

import com.sparta.delivery.domain.delivery.model.enums.DeliveryPersonType;
import com.sparta.delivery.infrastructure.configuration.auditing.listener.SoftDeleteListener;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.UUID;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "delivery_person")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class DeliveryPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_person_id")
    private Long deliveryPersonId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "slack_id")
    private String slackId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DeliveryPersonType type;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

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

    public static DeliveryPerson create(Long userId, UUID hubId, String slackId, DeliveryPersonType type) {
        return new DeliveryPerson(null, userId, hubId, slackId, type);
    }

    private DeliveryPerson(Long deliveryPersonId, Long userId, UUID hubId, String slackId, DeliveryPersonType type) {
        this.deliveryPersonId = deliveryPersonId;
        this.userId = userId;
        this.hubId = hubId;
        this.slackId = slackId;
        this.type = type;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

}

