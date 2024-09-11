package com.sparta.hub.domain.repository;

import com.sparta.hub.domain.model.Hub;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubCustomRepository {
    long countByIsDeletedFalse();
    Hub findByIdAndIsDeletedFalse(UUID id);
    List<Hub> findAllByIsDeletedFalse();
    Page<Hub> findAllByIsDeletedFalse(Pageable pageable);
    Page<Hub> searchByName(String name, Pageable pageable);
}
