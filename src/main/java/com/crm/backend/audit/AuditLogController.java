package com.crm.backend.audit;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogController(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String entityType,
            Authentication auth) {
        Long userId = getCurrentUserId(auth);
        List<AuditLog> logs;
        if (entityType != null && !entityType.isEmpty()) {
            logs = auditLogRepository.findByUserIdAndEntityTypeOrderByIdDesc(userId, entityType);
        } else {
            logs = auditLogRepository.findByUserIdOrderByIdDesc(userId);
        }
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getEntityAuditLogs(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            Authentication auth) {
        Long userId = getCurrentUserId(auth);
        return ResponseEntity.ok(
            auditLogRepository.findByUserIdAndEntityIdOrderByIdDesc(userId, entityId));
    }
}
