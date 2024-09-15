package com.sparta.delivery.infrastructure.persistence;

import com.sparta.delivery.domain.route.domain.model.DeliveryRoute;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRouteJpaRepository extends JpaRepository<DeliveryRoute, UUID> {
    List<DeliveryRoute> findByDeliveryDeliveryIdAndIsDeletedFalse(UUID deliveryId);
}
