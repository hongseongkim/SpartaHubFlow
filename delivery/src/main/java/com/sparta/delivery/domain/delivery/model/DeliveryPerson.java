package com.sparta.delivery.domain.delivery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "slack_id")
    private String slackId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DeliveryPersonType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    @JsonBackReference
    private Delivery delivery;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

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

    public static DeliveryPerson create(Long userId, String slackId, DeliveryPersonType type, Delivery delivery) {
        return new DeliveryPerson(null, userId, slackId, type, delivery);
    }

    private DeliveryPerson(Long deliveryPersonId, Long userId, String slackId, DeliveryPersonType type, Delivery delivery) {
        this.deliveryPersonId = deliveryPersonId;
        this.userId = userId;
        this.slackId = slackId;
        this.type = type;
        this.delivery = delivery;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted = true;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

}

