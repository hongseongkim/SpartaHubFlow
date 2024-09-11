package com.sparta.hub.domain.service;

import com.sparta.hub.application.dto.HubDto;
import com.sparta.hub.domain.model.Hub;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubService {
    Hub createHub(HubDto hubDto);
    Hub updateHub(UUID hubId, HubDto hubDto);
    void softDeleteHub(UUID hubId);
    Hub getHubById(UUID hubId);
    Page<Hub> getAllHubs(Pageable pageable);
    Page<Hub> searchHubsByName(String name, Pageable pageable);
}
