package com.sparta.map.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectionsResponse {
    private String summary;
    private long distanceInMeters;
    private long durationInSeconds;
}
