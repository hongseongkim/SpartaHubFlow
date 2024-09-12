package com.sparta.company.repository;

import com.sparta.company.domain.entity.CompanyAiChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AiChatRepository extends JpaRepository<CompanyAiChat, UUID> {
    @Query("SELECT a FROM CompanyAiChat a WHERE a.userId = :userId AND a.isDeleted = false")
    Page<CompanyAiChat> findChatForAUser(@Param("userId") Long userId, Pageable pageable);

}
