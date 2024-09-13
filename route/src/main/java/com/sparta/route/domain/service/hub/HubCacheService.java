package com.sparta.route.domain.service.hub;

import com.sparta.route.domain.dto.hub.HubDto;
import com.sparta.route.domain.dto.hub.HubResponseDto;
import com.sparta.route.infrastructure.client.HubFeignClient;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubCacheService {

    private final HubFeignClient hubFeignClient;

    public List<HubDto> getAllHubs() {

        log.info("허브 데이터를 요청합니다");

        List<HubResponseDto> hubResponses = hubFeignClient.getAllHubs();

        if (hubResponses == null || hubResponses.isEmpty()) {
            log.error("HubFeignClient에서 허브 데이터를 가져오지 못했습니다.");
            return List.of(); // 빈 리스트 반환
        }

        log.info("허브 데이터를 성공적으로 가져왔습니다. 응답 개수: {}", hubResponses.size());

        List<HubDto> hubDtos = hubResponses.stream()
                .map(HubDto::from)
                .filter(hubDto -> hubDto.getHubId() != null)  // null 체크
                .collect(Collectors.toList());

        log.info("허브 DTO 변환 완료. 개수: {}", hubDtos.size());

        return hubDtos;
    }

    public HubDto getHub(UUID hubId) {
        log.info("캐시에서 허브 데이터를 찾을 수 없으므로, 새로 데이터를 로드합니다.");
        List<HubDto> hubs = getAllHubs();

        return hubs.stream()
                .filter(hub -> hub.getHubId().equals(hubId))
                .findFirst()
                .orElse(null);
    }
}
