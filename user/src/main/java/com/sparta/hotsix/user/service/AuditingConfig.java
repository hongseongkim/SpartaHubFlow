package com.sparta.hotsix.user.service;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditingConfig implements AuditorAware<String> {

    // ThreadLocal을 사용해 현재 사용자의 정보를 관리
    private static final ThreadLocal<String> currentAuditor = new ThreadLocal<>();

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(currentAuditor.get());
    }

    // 현재 사용자를 설정하는 메서드
    public static void setCurrentAuditor(String auditor) {
        currentAuditor.set(auditor);
    }

    // 현재 사용자를 지우는 메서드 (필요에 따라 사용)
    public static void clear() {
        currentAuditor.remove();
    }
}