package com.sparta.route.infrastructure.persistence;

import com.sparta.route.domain.model.DeliveryRoute;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRouteJpaRepository extends JpaRepository<DeliveryRoute, UUID> {
    List<DeliveryRoute> findByDeliveryIdAndIsDeletedFalse(UUID deliveryId);
}
