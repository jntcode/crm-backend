package com.crm.backend.task;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdOrderByDueDateAsc(Long userId);
    List<Task> findByUserIdAndCustomerIdOrderByDueDateAsc(Long userId, String customerId);
    List<Task> findByUserIdAndStatusOrderByDueDateAsc(Long userId, TaskStatus status);
    long countByUserId(Long userId);
    long countByUserIdAndStatusNot(Long userId, TaskStatus status);
}
