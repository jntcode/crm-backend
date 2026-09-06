package com.crm.backend.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserIdOrderByIdDesc(Long userId);
    List<AuditLog> findByUserIdAndEntityTypeOrderByIdDesc(Long userId, String entityType);
    List<AuditLog> findByUserIdAndEntityIdOrderByIdDesc(Long userId, Long entityId);
}
