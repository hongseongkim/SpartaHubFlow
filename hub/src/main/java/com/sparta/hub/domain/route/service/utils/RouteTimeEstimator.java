package com.sparta.hub.domain.route.service.utils;

import com.sparta.hub.domain.hub.model.Hub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RouteTimeEstimator {
    public int calculateEstimatedTime(Hub originHub, Hub destinationHub) {
        double distance = calculateDistance(originHub, destinationHub);

        int baseTime = (int) (distance / 60.0 * 60); // 60km/h의 평균 속도 가정
        double trafficMultiplier = getTrafficMultiplier(LocalDateTime.now());
        int estimatedTime = (int) (baseTime * trafficMultiplier);

        // 최소 30분, 최대 8시간으로 제한
        return Math.max(30, Math.min(estimatedTime, 480));
    }

    private double calculateDistance(Hub origin, Hub destination) {
        // 위도와 경도를 이용한 두 지점 간 거리 계산 (Haversine 공식 사용)
        double earthRadius = 6371; // 지구의 반경 (km)
        double dLat = Math.toRadians(destination.getLatitude() - origin.getLatitude());
        double dLon = Math.toRadians(destination.getLongitude() - origin.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(origin.getLatitude())) * Math.cos(Math.toRadians(destination.getLatitude())) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

    // 시간대별 교통 상황 고려
    private double getTrafficMultiplier(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        if (hour >= 7 && hour <= 9) return 1.5; // 아침 러시아워
        if (hour >= 17 && hour <= 19) return 1.4; // 저녁 러시아워
        if (hour >= 22 || hour <= 5) return 0.8; // 심야 시간대
        return 1.0; // 그 외 시간대
    }
}
