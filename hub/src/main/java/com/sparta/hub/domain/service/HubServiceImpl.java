package com.sparta.hub.domain.service;

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
public class HubServiceImpl implements HubService {

    private final HubRepository hubRepository;
    private final MapServiceClient mapServiceClient;

    @Override
    @Transactional
    public Hub createHub(HubDto hubDto) {

        // 주소로부터 위도와 경도 정보를 가져옴
        Map<String, Double> coordinates = mapServiceClient.getCoordinates(hubDto.getAddress());

        Double latitude = coordinates.get("lat");
        Double longitude = coordinates.get("lng");

        validateCoordinates(latitude, longitude);
        validateNameAndAddress(hubDto.getName(), hubDto.getAddress());

        Hub hub = Hub.create(hubDto.getName(), hubDto.getAddress(), latitude, longitude);

        return hubRepository.save(hub);

    }

    @Override
    @Transactional
    public Hub updateHub(UUID hubId, HubDto hubDto) {

        validateNameAndAddress(hubDto.getName(), hubDto.getAddress());

        Hub hub = getHubById(hubId);
        hub.updateName(hubDto.getName());

        // 주소가 변경되었는지 확인
        if (!hub.getAddress().equals(hubDto.getAddress())) {

            hub.updateAddress(hubDto.getAddress());

            // 새 주소로부터 위도와 경도 정보를 가져옴
            Map<String, Double> coordinates = mapServiceClient.getCoordinates(hubDto.getAddress());

            Double latitude = coordinates.get("lat");
            Double longitude = coordinates.get("lng");

            validateCoordinates(latitude, longitude);
            hub.updateCoordinates(latitude, longitude);
        }

        return hubRepository.save(hub);
    }

    @Override
    @Transactional
    public void softDeleteHub(UUID hubId) {
        Hub hub = getHubById(hubId);
        hub.softDelete();
        hubRepository.save(hub);
    }

    @Override
    @Transactional(readOnly = true)
    public Hub getHubById(UUID hubId) {

        Hub hub = hubRepository.findByIdAndIsDeletedFalse(hubId);

        if (hub == null) {
            throw new ServiceException(ErrorCode.HUB_NOT_FOUND);
        }

        return hub;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Hub> getAllHubs(Pageable pageable) {
        return hubRepository.findAllByIsDeletedFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Hub> searchHubsByName(String name, Pageable pageable) {
        return hubRepository.searchByName(name, pageable);
    }

    private void validateNameAndAddress(String name, String address) {
        if (name == null || name.isEmpty()) {
            throw new ServiceException(ErrorCode.NULL_OR_EMPTY_VALUE);
        }
        if (address == null || address.isEmpty()) {
            throw new ServiceException(ErrorCode.NULL_OR_EMPTY_VALUE);
        }
    }

    private void validateCoordinates(Double latitude, Double longitude) {
        if (!isValidLatitude(latitude) || !isValidLongitude(longitude)) {
            throw new ServiceException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private boolean isValidLatitude(Double latitude) {
        return latitude != null && latitude >= -90 && latitude <= 90;
    }

    private boolean isValidLongitude(Double longitude) {
        return longitude != null && longitude >= -180 && longitude <= 180;
    }
}
