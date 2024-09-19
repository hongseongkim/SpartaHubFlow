package com.sparta.hub.application.initializer;

import com.sparta.hub.domain.hub.data.HubData;
import com.sparta.hub.domain.hub.data.HubDataLoader;
import com.sparta.hub.domain.map.model.Coordinates;
import com.sparta.hub.domain.map.service.MapService;
import com.sparta.hub.domain.hub.model.Hub;
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
public class HubInitializerService implements ApplicationRunner {

    private final MapService mapService;
    private final HubRouteService hubRouteService;

    private final HubRepository hubRepository;
    private final HubRouteRepository hubRouteRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            initializeHubs();
            initializeHubRoutes();
        } catch (Exception e) {
            log.error("허브 데이터를 초기화하는데 실패하였습니다.", e);
            throw new RuntimeException("허브 데이터를 초기화하는데 실패하였습니다.", e);
        }
    }

    private List<HubData> loadHubData() throws IOException {
        try {
            return HubDataLoader.getHubsData();
        } catch (IOException e) {
            log.error("허브 데이터를 로드하는데 실패했습니다.", e);
            throw new IOException("허브 데이터를 로드하는데 실패했습니다.", e);
        }
    }

    private void initializeHubs() throws IOException {
        if (hubRepository.count() == 0) {
            log.info("허브 초기화 시작");

            List<HubData> hubsData = loadHubData();

            for (HubData hubData : hubsData) {
                try {
                    Coordinates coordinates = mapService.getCoordinates(hubData.getAddress());
                    Hub hub = Hub.create(hubData.getName(), hubData.getAddress(), coordinates.getLatitude(),
                            coordinates.getLongitude());
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
        if (hubRouteRepository.count() == 0) {
            log.info("허브 경로 초기화 시작");

            List<Hub> hubs = hubRepository.findAll();
            if (hubs.size() != 17) {
                log.error("예상된 허브 수(17)와 실제 허브 수({})가 일치하지 않습니다.", hubs.size());
                return;
            }

            int totalRoutes = 0;
            for (int i = 0; i < hubs.size(); i++) {
                for (int j = 0; j < hubs.size(); j++) {
                    if (i != j) {
                        Hub originHub = hubs.get(i);
                        Hub destinationHub = hubs.get(j);

                        HubRoute route = hubRouteService.createHubRoute(originHub.getHubId(),
                                destinationHub.getHubId());
                        hubRouteRepository.save(route);
                        totalRoutes++;

                        log.debug("허브 경로 생성: {} -> {}", originHub.getName(), destinationHub.getName());
                    }
                }
            }

            log.info("허브 경로 초기화 완료. 총 {} 개의 경로가 생성되었습니다.", totalRoutes);
        } else {
            log.info("허브 경로가 이미 초기화되어 있습니다. 총 {} 개의 경로가 존재합니다.", hubRouteRepository.count());
        }
    }
}
