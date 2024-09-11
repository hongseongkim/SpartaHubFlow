package com.sparta.hub.application.service;

import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.infrastructure.repository.HubRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubCacheService {

    private final HubRepository hubRepository;

    @Cacheable(value = "hubs", key = "#hubId")
    @Transactional(readOnly = true)
    public Hub getHub(UUID hubId) {
        // 캐시에 값이 없으면 이 로직이 실행되고, 캐시에 값이 있으면 이 로직이 실행되지 않음
        return hubRepository.findByIdAndIsDeletedFalse(hubId);
    }

    @CachePut(value = "hubs", key = "#hubId")
    public Hub cacheHub(Hub hub) {
        // 캐시 갱신을 위해 사용하는 메서드
        return hub;
    }

    @CacheEvict(value = "hubs", key = "#hubId")
    public void evictHub(UUID hubId) {
        // 캐시에서 값 삭제
    }

    @Cacheable(value = "hubs_all", key = "'List<Hub>:' + #pageable.pageNumber + '_' + #pageable.pageSize", unless = "#result.empty")
    @Transactional(readOnly = true)
    public List<Hub> getAllHubs(Pageable pageable) {
        // 캐시에 값이 없으면 이 메서드가 실행되어 결과를 캐시에 저장
        Page<Hub> allHubs = hubRepository.findAllByIsDeletedFalse(pageable);
        return allHubs.getContent();
    }
}
