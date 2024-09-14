package com.sparta.hub.application.initializer;

import com.sparta.hub.domain.hub.data.HubData;
import com.sparta.hub.domain.hub.data.HubDataLoader;
import com.sparta.hub.domain.map.model.Coordinates;
import com.sparta.hub.domain.map.service.MapService;
import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.infrastructure.persistence.HubRepository;
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
    private final HubRepository hubRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            initializeHubs();
        } catch (Exception e) {
            log.error("Failed to initialize hubs", e);
            throw new RuntimeException("Application initialization failed", e);
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
}
