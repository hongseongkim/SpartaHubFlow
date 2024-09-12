package com.sparta.hotsix.product.repository;

import com.sparta.hotsix.product.domain.entity.ProductAiChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AiChatRepository extends JpaRepository<ProductAiChat, UUID> {
    @Query("SELECT a FROM ProductAiChat a WHERE a.userId = :userId AND a.isDeleted = false")
    Page<ProductAiChat> findChatForAUser(@Param("userId") Long userId, Pageable pageable);

}
