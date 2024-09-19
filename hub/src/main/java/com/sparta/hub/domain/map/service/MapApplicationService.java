package com.sparta.hub.domain.map.service;

import com.google.maps.model.TravelMode;
import com.sparta.hub.domain.map.model.Coordinates;
import com.sparta.hub.domain.map.dto.request.DirectionsRequest;
import com.sparta.hub.domain.map.dto.response.DirectionsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapApplicationService {

    private final MapService mapService;

    // 경도, 위도 좌표 요청
    public Coordinates getCoordinates(String address) throws Exception {
        return mapService.getCoordinates(address);
    }

    // 최적 경로 요청(경유지 추가)
    public DirectionsResponse getOptimizedDirections(String origin, String destination, List<String> waypoints, TravelMode mode) throws Exception {
        DirectionsRequest directionsRequest = new DirectionsRequest(origin, destination, waypoints, mode);
        return mapService.getDirections(directionsRequest);
    }
}
