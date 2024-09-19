package com.sparta.hub.domain.map.service;

import com.google.maps.errors.ApiException;
import com.sparta.hub.domain.map.dto.request.DirectionsRequest;
import com.sparta.hub.domain.map.dto.response.DirectionsResponse;
import com.sparta.hub.domain.map.model.Coordinates;
import java.io.IOException;

public interface MapService {
    Coordinates getCoordinates(String address);
    DirectionsResponse getDirections(DirectionsRequest request) throws IOException, InterruptedException, ApiException;
}
