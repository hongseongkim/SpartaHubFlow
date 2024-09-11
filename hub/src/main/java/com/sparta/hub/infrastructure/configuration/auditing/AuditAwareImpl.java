package com.sparta.hub.infrastructure.configuration.auditing;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuditAwareImpl implements AuditorAware<String> {
    @Override
    @Nonnull
    public Optional<String> getCurrentAuditor() {

        // 현재 요청 정보 가져오기
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();

            // 헤더에서 User-Id 값 추출
            String userId = request.getHeader("User-Id");

            if (userId != null && !userId.isEmpty()) {
                return Optional.of(userId); // user-id가 있으면 반환
            }
        }

        // 값이 없으면 "Unknown User" 반환
        return Optional.of("Unknown User");
    }
}

