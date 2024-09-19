package com.sparta.hub.infrastructure.persistence;

import com.sparta.hub.domain.route.model.HubRoute;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {
    Optional<HubRoute> findByHubRouteIdAndIsDeletedFalse(UUID hubRouteId);
    Optional<HubRoute> findByOriginHubIdAndDestinationHubIdAndIsDeletedFalse(UUID originHubId, UUID destinationHubId);

    Optional<List<HubRoute>> findByOriginHubIdAndIsDeletedFalse(UUID originHubId);
    Optional<List<HubRoute>> findByDestinationHubIdAndIsDeletedFalse(UUID destinationHubId);

    Page<HubRoute> findAllByIsDeletedFalse(Pageable pageable);

    // 특정 예상 소요 시간 이하의 경로 찾기
    List<HubRoute> findByEstimatedTimeLessThanEqualAndIsDeletedFalse(Integer estimatedTime);
}
