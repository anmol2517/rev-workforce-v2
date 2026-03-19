package com.revworkforce.service;

import com.revworkforce.entity.AuditLog;
import com.revworkforce.entity.User;
import com.revworkforce.repository.AuditLogRepository;
import com.revworkforce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public void logAction(String action, String entityType, Long entityId, String details) {
        try {
            User currentUser = userRepository.findAll()
                    .stream()
                    .filter(u -> u.getId().equals(getCurrentUserId()))
                    .findFirst()
                    .orElse(null);

            if (currentUser != null) {
                AuditLog auditLog = new AuditLog();
                auditLog.setCreatedBy(currentUser);
                auditLog.setAction(action);
                auditLog.setEntityType(entityType);
                auditLog.setEntityId(entityId);
                auditLog.setDetails(details);

                auditLogRepository.save(auditLog);
            }
        } catch (Exception e) {
        }
    }

    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<AuditLog> getUserAuditLogs(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return auditLogRepository.findByCreatedByOrderByCreatedAtDesc(user, pageable);
    }

    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found for Audit Log");
        }

        String email = auth.getName();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found in DB for Audit Log: " + email));
    }
}
