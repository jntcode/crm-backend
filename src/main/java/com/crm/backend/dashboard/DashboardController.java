package com.crm.backend.dashboard;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import com.crm.backend.customer.CustomerRepository;
import com.crm.backend.lead.LeadMonthlyValue;
import com.crm.backend.lead.LeadRepository;
import com.crm.backend.lead.LeadStatus;
import com.crm.backend.task.TaskRepository;
import com.crm.backend.task.TaskStatus;
import com.crm.backend.email.EmailRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
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

        double wonValueThisMonth = buildMonthlyRevenue(userId).getLast().revenue();

        DashboardStats stats = new DashboardStats(
            totalCustomers,
            totalLeads,
            openTasks,
            totalEmails,
            wonValueThisMonth,
            Math.round(conversionRate * 10.0) / 10.0
        );
        return ResponseEntity.ok(stats);
    }

    /**
     * Won lead value for the last 6 calendar months, grouped by lead creation
     * date (the CRM has no separate close-date field). Months without data are
     * returned with zero so charts always render a full, honest window.
     */
    @GetMapping("/revenue")
    public ResponseEntity<List<RevenuePoint>> getRevenue() {
        return ResponseEntity.ok(buildMonthlyRevenue(getCurrentUserId()));
    }

    private List<RevenuePoint> buildMonthlyRevenue(Long userId) {
        List<LeadMonthlyValue> sums =
                leadRepository.sumValueByCreationMonth(userId, LeadStatus.WON);

        Map<YearMonth, BigDecimal> byMonth = new HashMap<>();
        for (LeadMonthlyValue value : sums) {
            if (value.year() == null || value.month() == null) {
                continue;
            }
            byMonth.put(YearMonth.of(value.year(), value.month()),
                    value.total() == null ? BigDecimal.ZERO : value.total());
        }

        YearMonth current = YearMonth.now();
        List<RevenuePoint> points = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            BigDecimal total = byMonth.getOrDefault(month, BigDecimal.ZERO);
            points.add(new RevenuePoint(
                    month.toString(),
                    month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    total.setScale(2, RoundingMode.HALF_UP).doubleValue()));
        }
        return points;
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
