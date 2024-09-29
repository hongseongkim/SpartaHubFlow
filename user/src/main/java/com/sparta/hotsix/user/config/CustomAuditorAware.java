package com.sparta.hotsix.user.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class CustomAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String userIdHeader = request.getHeader("User-Id");

            if (userIdHeader != null) {
                try {
                    Long userId = Long.valueOf(userIdHeader);
                    return Optional.of(userId);
                } catch (NumberFormatException e) {
                    // 헤더가 있지만 숫자로 변환할 수 없는 경우
                    return Optional.empty();
                }
            }
        }

        // 헤더가 없거나 요청이 잘못된 경우
        return Optional.empty();
    }
}