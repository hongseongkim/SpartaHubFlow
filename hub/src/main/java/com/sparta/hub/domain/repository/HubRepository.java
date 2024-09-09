package com.sparta.hub.domain.repository;

import com.sparta.hub.domain.model.Hub;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubRepository extends JpaRepository<Hub, UUID>, HubCustomRepository {
    Page<Hub> findAllByDeletedAtIsNull(Pageable pageable);
}
