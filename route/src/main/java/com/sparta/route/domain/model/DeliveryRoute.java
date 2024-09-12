package com.sparta.route.domain.model;

import com.sparta.route.domain.model.enums.DeliveryRouteStatus;
import com.sparta.route.infrastructure.configuration.auditing.listener.SoftDeleteListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "p_delivery_route")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class DeliveryRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID deliveryRouteId;
    private UUID deliveryId;
    private Integer sequence;
    private UUID originHubId;
    private UUID destinationHubId;
    private Double estimatedDistance;
    private Integer estimatedTime;
    private Double actualDistance;
    private Integer actualTime;
    private DeliveryRouteStatus currentStatus;
    private Boolean isDeleted = false;

    public static DeliveryRoute create(UUID deliveryId, UUID originHubId, UUID destinationHubId) {
        return new DeliveryRoute(null, deliveryId, originHubId, destinationHubId);
    }

    private DeliveryRoute(UUID deliveryRouteId, UUID deliveryId, UUID originHubId, UUID destinationHubId) {
        this.deliveryId = deliveryId;
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
    }
}
