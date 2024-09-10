package com.sparta.map.presentation.dto.request;

import com.google.maps.model.TravelMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectionsRequest {
    private String origin;
    private String destination;
    private List<String> waypoints;
    private TravelMode mode;
}
