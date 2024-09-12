package com.sparta.route.infrastructure.initializer;

import com.sparta.route.domain.dto.hub.HubDto;
import com.sparta.route.domain.model.hubRoute.HubRoute;
import com.sparta.route.domain.service.hub.HubCacheService;
import com.sparta.route.infrastructure.persistence.HubRouteJpaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubRouteInitializerService implements ApplicationRunner {

    private final HubCacheService hubCacheService;
    private final HubRouteJpaRepository hubRouteJpaRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (hubRouteJpaRepository.count() == 0) {
            try {
                initializeHubRoutes();
            } catch (Exception e) {
                log.error("HubRoute 초기화 중 오류 발생: {}", e.getMessage());
            }
        }
    }

    public void initializeHubRoutes() {
        log.info("허브 라우트 초기화 시작");

        List<HubDto> allHubs = hubCacheService.getAllHubs();

        if (allHubs == null || allHubs.isEmpty()) {
            log.error("허브 데이터를 불러오지 못했습니다.");
            return;
        }

        log.info("불러온 허브 데이터 개수: {}", allHubs.size());
        log.info("허브 데이터를 성공적으로 불러왔습니다. 라우트 생성 시작");

        List<HubRoute> routes = createRoutes(allHubs);
        hubRouteJpaRepository.saveAll(routes);

        log.info("허브 라우트가 성공적으로 저장되었습니다.");
    }

    private List<HubRoute> createRoutes(List<HubDto> hubs) {
        List<HubRoute> routes = new ArrayList<>();
        for (int i = 0; i < hubs.size() - 1; i++) {
            HubDto originHub = hubs.get(i);
            HubDto destinationHub = hubs.get(i + 1);

            HubRoute route = HubRoute.create(
                    originHub.getHubId(),
                    destinationHub.getHubId(),
                    calculateEstimatedTime(originHub.getHubId(), destinationHub.getHubId()),
                    generateRouteDisplayName(originHub, destinationHub)
            );

            routes.add(route);
        }
        return routes;
    }

    private String generateRouteDisplayName(HubDto originHub, HubDto destinationHub) {
        return originHub.getName() + " -> " + destinationHub.getName();
    }

    private Integer calculateEstimatedTime(UUID originHubId, UUID destinationHubId) {
        return 30 + (int)(Math.random() * 90); // 임시 로직
    }
}
