package com.sparta.delivery.infrastructure.persistence;

import com.sparta.delivery.domain.model.Delivery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryJpaRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByDeliveryIdAndIsDeletedFalse(UUID deliveryId);

    List<Delivery> findAllByIsDeletedFalse();
}
