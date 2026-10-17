package com.crm.backend.lead;

import com.crm.backend.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByUserIdOrderByIdDesc(Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, LeadStatus status);

    /**
     * Single aggregate query (no N+1): total value of leads per creation
     * month for one user and status. Leads without created_at are excluded.
     */
    @Query("""
            SELECT new com.crm.backend.lead.LeadMonthlyValue(
                YEAR(l.createdAt), MONTH(l.createdAt), SUM(l.value))
            FROM Lead l
            WHERE l.user.id = :userId AND l.status = :status AND l.createdAt IS NOT NULL
            GROUP BY YEAR(l.createdAt), MONTH(l.createdAt)
            ORDER BY YEAR(l.createdAt), MONTH(l.createdAt)
            """)
    List<LeadMonthlyValue> sumValueByCreationMonth(@Param("userId") Long userId,
                                                   @Param("status") LeadStatus status);
}
