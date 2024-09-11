package com.sparta.hub.application.service;

import com.sparta.hub.application.exception.ErrorCode;
import com.sparta.hub.application.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    public void validateManagerRole(String userRole) {
        if (!isManagerRole(userRole)) {
            throw new ServiceException(ErrorCode.UNAUTHORIZED);
        }
    }

    private boolean isManagerRole(String userRole) {
        return "HUB_MANAGER".equals(userRole);
    }
}
