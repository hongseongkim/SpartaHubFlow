package com.sparta.order.repository;

import com.sparta.order.domain.entity.OrderAiChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AiChatRepository extends JpaRepository<OrderAiChat, UUID> {
    @Query("SELECT a FROM OrderAiChat a WHERE a.userId = :userId AND a.isDeleted = false")
    Page<OrderAiChat> findChatForAUser(@Param("userId") Long userId, Pageable pageable);

}
