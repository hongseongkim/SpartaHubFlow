package com.sparta.hub.domain.repository;

import com.sparta.hub.domain.model.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubCustomRepository {
    Page<Hub> searchByName(String name, Pageable pageable);
}
