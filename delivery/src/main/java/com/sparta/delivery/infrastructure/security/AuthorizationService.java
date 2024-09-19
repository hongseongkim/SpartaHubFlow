package com.sparta.delivery.infrastructure.security;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    // 1. 마스터 권한 검증 (최고 권한자만 수행 가능한 작업)
    public void validateMasterRole(String userRole) {
        if (!isMasterRole(userRole)) {
            throw new ServiceException("권한 부족: 마스터 권한이 필요합니다.");
        }
    }

    // 2. 허브 관리자 권한 검증 + 소속 허브와 요청 허브 ID 일치 여부 확인
    public void validateManagerRole(String userRole, UUID hubId, UUID userHubId) {
        if (!isManagerRole(userRole)) {
            throw new ServiceException("권한 부족: 허브 관리자 권한이 필요합니다.");
        }
        validateHubAccess(hubId, userHubId);
    }

    // 3. 배송 담당자 권한 검증 + 본인 여부 확인
    public void validateDeliveryPersonnelRole(String userRole, UUID deliveryPersonId, UUID userId) {
        if (!isDeliveryPersonnelRole(userRole)) {
            throw new ServiceException("권한 부족: 배송 담당자 권한이 필요합니다.");
        }
        validateUserAccess(deliveryPersonId, userId);
    }

    // 4. 허브 관리자 또는 마스터 권한 검증
    public void validateManagerOrMasterRole(String userRole, UUID hubId, UUID userHubId) {
        if (isMasterRole(userRole)) {
            return; // 마스터 권한이면 바로 통과
        }
        validateManagerRole(userRole, hubId, userHubId); // 허브 관리자 권한 검증
    }

    // 5. 허브에 접근 권한 있는지 확인 (허브 관리자일 때)
    private void validateHubAccess(UUID hubId, UUID userHubId) {
        if (!hubId.equals(userHubId)) {
            throw new ServiceException("권한 부족: 허브 ID가 일치하지 않습니다.");
        }
    }

    // 6. 본인 확인 (배송 담당자일 때)
    private void validateUserAccess(UUID entityId, UUID userId) {
        if (!entityId.equals(userId)) {
            throw new ServiceException("권한 부족: 본인만 접근 가능합니다.");
        }
    }

    // 역할 확인 메서드 (private)
    private boolean isMasterRole(String userRole) {
        return "MASTER".equals(userRole);
    }

    private boolean isManagerRole(String userRole) {
        return "HUB_MANAGER".equals(userRole);
    }

    private boolean isDeliveryPersonnelRole(String userRole) {
        return "DELIVERY_PERSON".equals(userRole);
    }
}
