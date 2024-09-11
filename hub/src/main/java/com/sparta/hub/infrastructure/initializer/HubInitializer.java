package com.sparta.hub.infrastructure.initializer;

import com.sparta.hub.domain.model.Hub;
import com.sparta.hub.infrastructure.client.MapServiceClient;
import com.sparta.hub.infrastructure.repository.HubRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubInitializer implements CommandLineRunner {

    private final HubRepository hubRepository;
    private final MapServiceClient mapServiceClient;

    @Override
    public void run(String... args) throws Exception {
        if (hubRepository.count() == 0) {
            // 허브 데이터
            List<String[]> hubsData = List.of(
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

            for (String[] hubData : hubsData) {
                String name = hubData[0];
                String address = hubData[1];

                try {
                    // 주소로부터 좌표 정보 가져오기
                    Map<String, Double> coordinates = mapServiceClient.getCoordinates(address);
                    Double latitude = coordinates.get("lat");
                    Double longitude = coordinates.get("lng");

                    // 허브 생성 및 저장
                    Hub hub = Hub.create(name, address, latitude, longitude);
                    hubRepository.save(hub);

                } catch (Exception e) {
                    // 좌표 정보를 가져오지 못한 경우 로그 출력
                    log.error("Failed to get coordinates for address: " + address + ". Error: " + e.getMessage());
                }
            }
        }
    }
}
