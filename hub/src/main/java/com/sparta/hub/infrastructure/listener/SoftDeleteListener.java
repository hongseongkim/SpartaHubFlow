package com.sparta.hub.infrastructure.listener;

import com.sparta.hub.domain.model.Hub;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SoftDeleteListener {
    @PreUpdate
    public void preUpdate(Object object) {
        if (object instanceof Hub hub && (hub.getDeletedAt() != null && hub.getDeletedBy() == null)) {
                hub.setDeletedBy(getCurrentAuditor());
        }
    }

    private String getCurrentAuditor() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
