package com.sparta.hub.infrastructure.listener;

import com.sparta.hub.domain.model.Hub;
import jakarta.persistence.PreUpdate;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SoftDeleteListener {
    @PreUpdate
    public void preUpdate(Object object) {
        if (object instanceof Hub hub && (hub.getDeletedAt() != null && hub.getDeletedBy() == null)) {
            hub.setDeletedBy(getCurrentAuditor().orElse("Unknown User"));
        }
    }

    private Optional<String> getCurrentAuditor() {
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
