package com.sparta.hub.infrastructure.persistence;

import com.sparta.hub.domain.route.model.HubRoute;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {
    Optional<HubRoute> findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(UUID originHubId, UUID destinationHubId);

    List<HubRoute> findAllByIsDeletedFalse();

    List<HubRoute> findByOriginHubIdAndIsDeletedFalse(UUID originHubId);

    List<HubRoute> findByDestinationHubIdAndIsDeletedFalse(UUID destinationHubId);

    boolean existsByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(UUID originHubId, UUID destinationHubId);

    // 특정 예상 소요 시간 이하의 경로 찾기
    List<HubRoute> findByEstimatedTimeLessThanEqualAndIsDeletedFalse(Integer estimatedTime);

    // 특정 허브에서 출발하거나 도착하는 모든 경로 찾기
    List<HubRoute> findByOriginHubIdOrDestinationHubIdAndIsDeletedFalse(UUID hubId, UUID sameHubId);
}
