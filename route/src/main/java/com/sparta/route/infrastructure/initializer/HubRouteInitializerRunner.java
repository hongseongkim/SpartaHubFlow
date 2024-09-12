package com.sparta.route.infrastructure.initializer;

import com.sparta.route.infrastructure.persistence.HubRouteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HubRouteInitializerRunner implements CommandLineRunner {

    private final HubRouteInitializerService hubRouteInitializerService;
    private final HubRouteJpaRepository hubRouteJpaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (hubRouteJpaRepository.count() == 0) {
            hubRouteInitializerService.initializeHubRoutes();
        }
    }
}
