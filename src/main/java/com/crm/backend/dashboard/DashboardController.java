package com.crm.backend.dashboard;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import com.crm.backend.customer.CustomerRepository;
import com.crm.backend.lead.LeadRepository;
import com.crm.backend.lead.LeadStatus;
import com.crm.backend.task.TaskRepository;
import com.crm.backend.task.TaskStatus;
import com.crm.backend.email.EmailRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final TaskRepository taskRepository;
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public DashboardController(CustomerRepository customerRepository, LeadRepository leadRepository,
                               TaskRepository taskRepository, EmailRepository emailRepository,
                               UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.taskRepository = taskRepository;
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        Long userId = getCurrentUserId();
        long totalCustomers = customerRepository.countByUserId(userId);
        long totalLeads = leadRepository.countByUserId(userId);
        long openTasks = taskRepository.countByUserIdAndStatusNot(userId, TaskStatus.DONE);
        long totalEmails = emailRepository.countByUserId(userId);

        long wonLeads = leadRepository.countByUserIdAndStatus(userId, LeadStatus.WON);
        double conversionRate = totalLeads > 0 ? (double) wonLeads / totalLeads * 100 : 0;

        DashboardStats stats = new DashboardStats(
            totalCustomers,
            totalLeads,
            openTasks,
            totalEmails,
            0.0,
            Math.round(conversionRate * 10.0) / 10.0
        );
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/pipeline-breakdown")
    public ResponseEntity<PipelineBreakdown> getPipelineBreakdown() {
        Long userId = getCurrentUserId();
        PipelineBreakdown breakdown = new PipelineBreakdown();
        breakdown.setNew(leadRepository.countByUserIdAndStatus(userId, LeadStatus.NEW));
        breakdown.setContacted(leadRepository.countByUserIdAndStatus(userId, LeadStatus.CONTACTED));
        breakdown.setQualified(leadRepository.countByUserIdAndStatus(userId, LeadStatus.QUALIFIED));
        breakdown.setProposal(leadRepository.countByUserIdAndStatus(userId, LeadStatus.PROPOSAL));
        breakdown.setWon(leadRepository.countByUserIdAndStatus(userId, LeadStatus.WON));
        breakdown.setLost(leadRepository.countByUserIdAndStatus(userId, LeadStatus.LOST));
        return ResponseEntity.ok(breakdown);
    }
}
