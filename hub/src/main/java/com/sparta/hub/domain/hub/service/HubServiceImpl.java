package com.sparta.hub.domain.hub.service;

import com.sparta.hub.domain.map.model.Coordinates;
import com.sparta.hub.domain.map.service.MapService;
import com.sparta.hub.domain.hub.dtos.HubDto;
import com.sparta.hub.domain.hub.service.cache.HubCacheService;
import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.application.exception.ErrorCode;
import com.sparta.hub.application.exception.ServiceException;
import com.sparta.hub.infrastructure.persistence.HubRepository;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {

    private final MapService mapService;
    private final HubCacheService hubCacheService;
    private final HubRepository hubRepository;

    @Override
    @Transactional
    public Hub createHub(HubDto hubDto) {

        Coordinates coordinates = mapService.getCoordinates(hubDto.getAddress());

        Double latitude = coordinates.getLatitude();
        Double longitude = coordinates.getLongitude();

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

            Coordinates coordinates = mapService.getCoordinates(hubDto.getAddress());

            Double latitude = coordinates.getLatitude();
            Double longitude = coordinates.getLongitude();

            validateCoordinates(latitude, longitude);
            hub.updateCoordinates(latitude, longitude);
        }

        Hub updatedHub = hubRepository.save(hub);
        return hubCacheService.cacheHub(updatedHub);
    }

    @Override
    public Hub assignHubManager(UUID hubId, HubDto hubDto) {

        Hub hub = getHubById(hubId);

        hub.updateHubManager(hubDto.getHubManagerId(), hubDto.getHubManagerSlackId());

        Hub updatedHub = hubRepository.save(hub);
        return hubCacheService.cacheHub(updatedHub);
    }

    @Override
    @Transactional
    public void softDeleteHub(UUID hubId) {
        Hub hub = getHubById(hubId);
        hub.softDelete();
        hubRepository.save(hub);
        hubCacheService.evictHub(hubId);
    }

    @Override
    @Transactional(readOnly = true)
    public Hub getHubById(UUID hubId) {
        return hubCacheService.getHub(hubId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Hub> getAllHubs(Pageable pageable) {
        List<Hub> hubs = hubCacheService.getAllHubs(pageable);
        long total = hubRepository.countByIsDeletedFalse();
        return new PageImpl<>(hubs, pageable, total);
    }

    @Transactional(readOnly = true)
    public List<Hub> getAllHubsWithoutPagination() {
        return hubRepository.findAllByIsDeletedFalse();
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
