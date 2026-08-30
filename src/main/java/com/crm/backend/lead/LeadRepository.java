package com.crm.backend.lead;

import com.crm.backend.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByUserIdOrderByIdDesc(Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, LeadStatus status);
}
