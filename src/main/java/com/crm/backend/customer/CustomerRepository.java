package com.crm.backend.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
}
