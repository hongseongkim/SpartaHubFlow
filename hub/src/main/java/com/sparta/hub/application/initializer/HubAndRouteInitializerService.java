package com.sparta.hub.application.initializer;

import com.sparta.hub.domain.hub.data.HubData;
import com.sparta.hub.domain.hub.data.HubDataLoader;
import com.sparta.hub.domain.map.model.Coordinates;
import com.sparta.hub.domain.map.service.MapService;
import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.domain.hub.service.HubServiceImpl;
import com.sparta.hub.domain.route.dto.HubRouteRequestDto;
import com.sparta.hub.domain.route.dto.HubRouteResponseDto;
import com.sparta.hub.domain.route.model.HubRoute;
import com.sparta.hub.domain.route.service.HubRouteService;
import com.sparta.hub.infrastructure.persistence.HubRepository;
import com.sparta.hub.infrastructure.persistence.HubRouteRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubAndRouteInitializerService implements ApplicationRunner {
    private final HubServiceImpl hubService;
    private final HubRouteService hubRouteService;
    private final MapService mapService;
    private final HubRepository hubRepository;
    private final HubRouteRepository hubRouteJpaRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            initializeHubs();
            initializeHubRoutes();
        } catch (Exception e) {
            log.error("Failed to initialize hubs and routes", e);
            throw new RuntimeException("Application initialization failed", e);
        }
    }

    private List<HubData> loadHubData() throws IOException {
        try {
            return HubDataLoader.getHubsData();
        } catch (IOException e) {
            log.error("Failed to load hub data", e);
            throw new IOException("Hub data loading failed", e);
        }
    }

    private void initializeHubs() throws IOException {
        if (hubRepository.count() == 0) {
            log.info("허브 초기화 시작");

            List<HubData> hubsData = loadHubData();

            for (HubData hubData : hubsData) {
                try {
                    Coordinates coordinates = mapService.getCoordinates(hubData.getAddress());
                    Hub hub = Hub.create(hubData.getName(), hubData.getAddress(), coordinates.getLatitude(), coordinates.getLongitude());
                    hubRepository.save(hub);
                    log.info("허브 생성: {}", hub.getName());
                } catch (Exception e) {
                    log.error("허브를 생성에 실패했습니다: {}. Error: {}", hubData.getName(), e.getMessage());
                }
            }
            log.info("허브 초기화 완료. 총 {} 개의 허브가 생성되었습니다.", hubRepository.count());
        } else {
            log.info("허브가 이미 초기화되어 있습니다. 총 {} 개의 허브가 존재합니다.", hubRepository.count());
        }
    }

    private void initializeHubRoutes() {
        if (hubRouteJpaRepository.count() == 0) {
            log.info("허브 라우트 초기화 시작");

            List<Hub> allHubs = hubService.getAllHubsWithoutPagination();

            if (allHubs.isEmpty()) {
                log.warn("허브 데이터가 없습니다. 허브 경로를 생성할 수 없습니다.");
                return;
            }

            log.info("불러온 허브 데이터 개수: {}", allHubs.size());

            List<HubRoute> routes = new ArrayList<>();
            for (int i = 0; i < allHubs.size(); i++) {
                for (int j = i + 1; j < allHubs.size(); j++) {
                    Hub originHub = allHubs.get(i);
                    Hub destinationHub = allHubs.get(j);
                    try {
                        HubRoute route = hubRouteService.createHubRoute(originHub.getHubId(), destinationHub.getHubId());
                        routes.add(route);
                        log.info("허브 라우트 생성: {} -> {}", originHub.getName(), destinationHub.getName());
                    } catch (Exception e) {
                        log.error("허브 라우트 생성 실패: {} -> {}. Error: {}", originHub.getName(), destinationHub.getName(), e.getMessage());
                    }
                }
            }

            hubRouteJpaRepository.saveAll(routes);

            log.info("허브 라우트 초기화 완료. 총 {} 개의 라우트가 생성되었습니다.", routes.size());
        } else {
            log.info("허브 라우트가 이미 초기화되어 있습니다. 총 {} 개의 라우트가 존재합니다.", hubRouteJpaRepository.count());
        }
    }

}
