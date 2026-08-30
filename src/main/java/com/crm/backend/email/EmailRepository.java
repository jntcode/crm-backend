package com.crm.backend.email;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findByUserIdOrderBySentAtDesc(Long userId);
    List<Email> findByUserIdAndCustomerIdOrderBySentAtDesc(Long userId, String customerId);
    List<Email> findByUserIdAndStatusOrderBySentAtDesc(Long userId, EmailStatus status);
    long countByUserId(Long userId);
}
