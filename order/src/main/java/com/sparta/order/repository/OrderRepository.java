package com.sparta.order.repository;

import com.sparta.order.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId AND o.isDeleted = false")
    Order findByIdNotDeleted(@Param("orderId") UUID orderId);

    Page<Order> findAllByIsDeletedFalse(Pageable pageable);

    @Modifying
    @Query("UPDATE Order o SET o.deletedAt = :deletedAt, o.deletedBy = :deletedBy, o.isDeleted = true " +
            "WHERE o.orderId = :orderId")
    void deleteOrderById(@Param("orderId") UUID orderId, @Param("deletedAt") LocalDateTime deletedAt,
                         @Param("deletedBy") Long deletedBy);
}
