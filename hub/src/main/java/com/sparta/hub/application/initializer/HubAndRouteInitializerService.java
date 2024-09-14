package com.sparta.hub.application.initializer;

import com.sparta.hub.domain.map.model.Coordinates;
import com.sparta.hub.domain.map.service.MapService;
import com.sparta.hub.domain.hub.model.Hub;
import com.sparta.hub.domain.hub.service.HubServiceImpl;
import com.sparta.hub.domain.route.model.HubRoute;
import com.sparta.hub.domain.route.service.HubRouteService;
import com.sparta.hub.infrastructure.persistence.HubRepository;
import com.sparta.hub.infrastructure.persistence.HubRouteRepository;
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
        initializeHubs();
        initializeHubRoutes();
    }

    private void initializeHubs() {
        if (hubRepository.count() == 0) {

            log.info("허브 초기화 시작");

            List<String[]> hubsData = getHubsData();

            for (String[] hubData : hubsData) {
                String name = hubData[0];
                String address = hubData[1];

                try {
                    Coordinates coordinates = mapService.getCoordinates(address);
                    Double latitude = coordinates.getLatitude();
                    Double longitude = coordinates.getLongitude();

                    Hub hub = Hub.create(name, address, latitude, longitude);
                    hubRepository.save(hub);

                } catch (Exception e) {
                    log.error("Failed to get coordinates for address: " + address + ". Error: " + e.getMessage());
                }
            }
            log.info("허브 초기화 완료");
        }
    }

    private void initializeHubRoutes() {
        if (hubRouteJpaRepository.count() == 0) {

            log.info("허브 라우트 초기화 시작");

            List<Hub> allHubs = hubService.getAllHubsWithoutPagination();

            log.info("불러온 허브 데이터 개수: {}", allHubs.size());

            List<HubRoute> routes = createRoutes(allHubs);
            hubRouteJpaRepository.saveAll(routes);

            log.info("허브 라우트가 성공적으로 저장되었습니다.");
        }
    }

    private List<HubRoute> createRoutes(List<Hub> hubs) {
        List<HubRoute> routes = new ArrayList<>();

        for (int i = 0; i < hubs.size() - 1; i++) {
            Hub originHub = hubs.get(i);
            Hub destinationHub = hubs.get(i + 1);

            HubRoute route = HubRoute.create(
                    originHub.getHubId(),
                    destinationHub.getHubId(),
                    hubRouteService.calculateEstimatedTime(originHub.getHubId(), destinationHub.getHubId()),
                    hubRouteService.generateRouteDisplayName(originHub.getName(), destinationHub.getName())
            );

            routes.add(route);
        }
        return routes;
    }

    private List<String[]> getHubsData() {
        return List.of(
                new String[]{"서울특별시 센터", "서울특별시 송파구 송파대로 55"},
                new String[]{"경기 북부 센터", "경기도 고양시 덕양구 권율대로 570"},
                new String[]{"경기 남부 센터", "경기도 이천시 덕평로 257-21"},
                new String[]{"부산광역시 센터", "부산 동구 중앙대로 206"},
                new String[]{"대구광역시 센터", "대구 북구 태평로 161"},
                new String[]{"인천광역시 센터", "인천 남동구 정각로 29"},
                new String[]{"광주광역시 센터", "광주 서구 내방로 111"},
                new String[]{"대전광역시 센터", "대전 서구 둔산로 100"},
                new String[]{"울산광역시 센터", "울산 남구 중앙로 201"},
                new String[]{"세종특별자치시 센터", "세종특별자치시 한누리대로 2130"},
                new String[]{"강원특별자치도 센터", "강원특별자치도 춘천시 중앙로 1"},
                new String[]{"충청북도 센터", "충북 청주시 상당구 상당로 82"},
                new String[]{"충청남도 센터", "충남 홍성군 홍북읍 충남대로 21"},
                new String[]{"전북특별자치도 센터", "전북특별자치도 전주시 완산구 효자로 225"},
                new String[]{"전라남도 센터", "전남 무안군 삼향읍 오룡길 1"},
                new String[]{"경상북도 센터", "경북 안동시 풍천면 도청대로 455"},
                new String[]{"경상남도 센터", "경남 창원시 의창구 중앙대로 300"}
        );
    }
}
