package com.sparta.delivery.infrastructure.persistence;

import com.sparta.delivery.domain.delivery.model.DeliveryPerson;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPersonJpaRepository extends JpaRepository<DeliveryPerson, Long> {
}
