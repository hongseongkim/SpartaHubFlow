package com.sparta.hub.domain.route.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RouteInfo {
    private final int totalEstimatedTime;
    private final double totalEstimatedDistance;
}
