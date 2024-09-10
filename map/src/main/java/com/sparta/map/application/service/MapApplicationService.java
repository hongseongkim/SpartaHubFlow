package com.sparta.map.application.service;

import com.google.maps.model.TravelMode;
import com.sparta.map.domain.model.Coordinates;
import com.sparta.map.domain.repository.MapRepository;
import com.sparta.map.presentation.dto.request.DirectionsRequest;
import com.sparta.map.presentation.dto.response.DirectionsResponse;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MapApplicationService {

    private final MapRepository mapRepository;

    // 경로 요청
    public DirectionsResponse getDirections(DirectionsRequest request) throws Exception {
        return mapRepository.getDirections(request);
    }

    // 좌표 요청
    public Map<String, Double> getCoordinates(String address) throws Exception {
        Coordinates coordinates = mapRepository.getCoordinates(address);
        return Map.of(
                "lat", coordinates.getLatitude(),
                "lng", coordinates.getLongitude()
        );
    }

    // 여러 주소의 좌표를 한번에 요청 (batch 요청)
    public Map<String, Map<String, Double>> getMultipleCoordinates(List<String> addresses) throws Exception {
        Map<String, Map<String, Double>> coordinatesMap = new HashMap<>();

        for (String address : addresses) {
            Coordinates coordinates = mapRepository.getCoordinates(address);
            coordinatesMap.put(address, Map.of(
                    "lat", coordinates.getLatitude(),
                    "lng", coordinates.getLongitude()
            ));
        }

        return coordinatesMap;
    }

    // 최적 경로 요청(경유지 추가)
    public DirectionsResponse getOptimizedDirections(String origin, String destination, List<String> waypoints, TravelMode mode) throws Exception {
        DirectionsRequest directionsRequest = new DirectionsRequest(origin, destination, waypoints, mode);
        return mapRepository.getDirections(directionsRequest);
    }
}
