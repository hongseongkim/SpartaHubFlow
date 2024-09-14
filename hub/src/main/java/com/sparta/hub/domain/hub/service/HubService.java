package com.sparta.hub.domain.hub.service;

import com.sparta.hub.domain.hub.dtos.HubDto;
import com.sparta.hub.domain.hub.model.Hub;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubService {
    Hub createHub(HubDto hubDto) throws Exception;
    Hub updateHub(UUID hubId, HubDto hubDto) throws Exception;
    Hub assignHubManager(UUID hubId, HubDto hubDto);
    void softDeleteHub(UUID hubId);
    Hub getHubById(UUID hubId);
    Page<Hub> getAllHubs(Pageable pageable);
    Page<Hub> searchHubsByName(String name, Pageable pageable);
}
