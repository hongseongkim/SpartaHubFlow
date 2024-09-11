package com.sparta.route.infrastructure.initializer;

import com.sparta.route.domain.dto.HubResponseDto;
import com.sparta.route.domain.model.HubRoute;
import com.sparta.route.domain.service.HubRouteService;
import com.sparta.route.infrastructure.client.HubFeignClient;
import com.sparta.route.infrastructure.persistence.HubRouteJpaRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubRouteInitializerService {

    private final HubFeignClient hubFeignClient;
    private final HubRouteService hubRouteService;
    private final HubRouteJpaRepository hubRouteJpaRepository;

    @Transactional
    public void initializeHubRoutes() {
        List<HubResponseDto> allHubs = hubFeignClient.getAllHubs();
        List<HubRoute> routes = createRoutes(allHubs);
        hubRouteJpaRepository.saveAll(routes);
    }

    private List<HubRoute> createRoutes(List<HubResponseDto> hubs) {
        List<HubRoute> routes = new ArrayList<>();
        for (int i = 0; i < hubs.size() - 1; i++) {
            HubRoute route = HubRoute.create(hubs.get(i).getId(), hubs.get(i + 1).getId());
            Integer estimatedTime = hubRouteService.calculateEstimatedTime(
                    hubs.get(i).getId(), hubs.get(i + 1).getId());
            route.updateEstimatedTime(estimatedTime);
            routes.add(route);
        }
        return routes;
    }
}
