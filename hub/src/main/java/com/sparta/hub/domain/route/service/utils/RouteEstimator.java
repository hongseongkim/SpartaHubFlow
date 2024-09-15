package com.sparta.hub.domain.route.service.utils;

import com.sparta.hub.domain.hub.model.Hub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteEstimator {
    public int calculateEstimatedTime(Hub originHub, Hub destinationHub) {
        if (originHub == null || destinationHub == null) {
            log.error("Origin or destination hub is null");
            throw new IllegalArgumentException("Origin and destination hubs must not be null");
        }

        try {
            double distance = calculateDistance(originHub, destinationHub);
            int baseTime = (int) (distance / 60.0 * 60); // 60km/h의 평균 속도 가정
            double trafficMultiplier = getTrafficMultiplier(LocalDateTime.now());
            int estimatedTime = (int) (baseTime * trafficMultiplier);

            // 최소 30분, 최대 8시간으로 제한
            return Math.max(30, Math.min(estimatedTime, 480));
        } catch (Exception e) {
            log.error("Error calculating estimated time: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate estimated time", e);
        }
    }

    public double calculateDistance(Hub origin, Hub destination) {
        if (origin == null || destination == null) {
            log.error("Origin or destination hub is null");
            throw new IllegalArgumentException("Origin and destination hubs must not be null");
        }

        try {
            double earthRadius = 6371; // 지구의 반경 (km)
            double dLat = Math.toRadians(destination.getLatitude() - origin.getLatitude());
            double dLon = Math.toRadians(destination.getLongitude() - origin.getLongitude());
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(origin.getLatitude())) * Math.cos(Math.toRadians(destination.getLatitude())) *
                            Math.sin(dLon/2) * Math.sin(dLon/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            return earthRadius * c;
        } catch (Exception e) {
            log.error("Error calculating distance: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate distance", e);
        }
    }

    private double getTrafficMultiplier(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        if (hour >= 7 && hour <= 9) return 1.5; // 아침 러시아워
        if (hour >= 17 && hour <= 19) return 1.4; // 저녁 러시아워
        if (hour >= 22 || hour <= 5) return 0.8; // 심야 시간대
        return 1.0; // 그 외 시간대
    }
}
