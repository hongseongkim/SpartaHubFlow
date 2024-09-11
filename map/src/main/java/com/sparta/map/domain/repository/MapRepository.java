package com.sparta.map.domain.repository;

import com.sparta.map.domain.model.Coordinates;
import com.sparta.map.presentation.dto.request.DirectionsRequest;
import com.sparta.map.presentation.dto.response.DirectionsResponse;

public interface MapRepository {
    Coordinates getCoordinates(String address) throws Exception;
    DirectionsResponse getDirections(DirectionsRequest request) throws Exception;
}
