package com.sparta.delivery.domain.delivery.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_delivery_route_segments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "segment_id")
    private UUID segmentId;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "address")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_route_id")
    @JsonBackReference
    private DeliveryRoute deliveryRoute;

    // 허브 경로일 경우 생성자
    public RouteSegment(UUID hubId) {
        this.hubId = hubId;
    }

    // 배송지 경로일 경우 생성자
    public RouteSegment(String address) {
        this.address = address;
    }

    public void updateDeliveryRoute(DeliveryRoute route) {
        this.deliveryRoute = route;
    }
}
