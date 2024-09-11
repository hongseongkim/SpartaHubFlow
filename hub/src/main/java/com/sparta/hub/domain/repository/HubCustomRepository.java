package com.sparta.hub.domain.repository;

import com.sparta.hub.domain.model.Hub;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubCustomRepository {
    long countByIsDeletedFalse();
    Hub findByIdAndIsDeletedFalse(UUID id);
    Page<Hub> findAllByIsDeletedFalse(Pageable pageable);
    Page<Hub> searchByName(String name, Pageable pageable);
}
