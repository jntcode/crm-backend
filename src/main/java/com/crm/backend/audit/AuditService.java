package com.crm.backend.audit;

import com.crm.backend.auth.User;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(User user, String entityType, Long entityId, String action, String details) {
        AuditLog log = new AuditLog(user, entityType, entityId, action, details);
        auditLogRepository.save(log);
    }
}
