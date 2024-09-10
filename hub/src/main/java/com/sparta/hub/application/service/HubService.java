package com.sparta.hub.application.service;

import com.sparta.hub.application.dto.HubDto;
import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.application.exception.ErrorCode;
import com.sparta.hub.application.exception.ServiceException;
import com.sparta.hub.infrastructure.repository.HubRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    @Transactional
    public Hub createHub(HubDto hubDto) {
        Hub hub = Hub.create(hubDto.getName(), hubDto.getAddress());
        return hubRepository.save(hub);
    }

    @Transactional(readOnly = true)
    public Hub getHubById(UUID hubId) {
        return hubRepository.findById(hubId)
                .orElseThrow(() -> new ServiceException(ErrorCode.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Hub> getAllHubs(Pageable pageable) {
        return hubRepository.findAllByDeletedAtIsNull(pageable);
    }

    @Transactional
    public Hub updateHub(UUID hubId, HubDto hubDto) {
        Hub hub = getHubById(hubId);
        hub.updateName(hub.getName());
        hub.updateAddress(hubDto.getAddress());
        return hubRepository.save(hub);
    }

    @Transactional
    public void softDeleteHub(UUID hubId) {
        Hub hub = getHubById(hubId);
        hub.softDelete();
        hubRepository.save(hub);
    }

    @Transactional(readOnly = true)
    public Page<Hub> searchHubsByName(String name, Pageable pageable) {
        return hubRepository.searchByName(name, pageable);
    }
}
