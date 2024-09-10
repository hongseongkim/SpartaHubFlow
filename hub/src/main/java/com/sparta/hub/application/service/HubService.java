package com.sparta.hub.application.service;

import com.sparta.hub.application.dto.HubDto;
import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.application.exception.ErrorCode;
import com.sparta.hub.application.exception.ServiceException;
import com.sparta.hub.infrastructure.client.MapServiceClient;
import com.sparta.hub.infrastructure.repository.HubRepository;
import java.util.Map;
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
    private final MapServiceClient mapServiceClient;

    @Transactional
    public Hub createHub(HubDto hubDto) {

        // 주소로부터 위도와 경도 정보를 가져옴
        Map<String, Double> coordinates = mapServiceClient.getCoordinates(hubDto.getAddress());

        // 위도와 경도 값을 가져와 허브 객체 생성
        Double latitude = coordinates.get("lat");
        Double longitude = coordinates.get("lng");

        Hub hub = Hub.create(hubDto.getName(), hubDto.getAddress(), latitude, longitude);

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
