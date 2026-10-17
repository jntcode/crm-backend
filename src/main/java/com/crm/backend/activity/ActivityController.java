package com.crm.backend.activity;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ActivityController(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        Long userId = getCurrentUserId();
        List<ActivityDTO> activities = activityRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(ActivityDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByCustomer(@PathVariable String customerId) {
        Long userId = getCurrentUserId();
        List<ActivityDTO> activities = activityRepository.findByUserIdAndCustomerIdOrderByCreatedAtDesc(userId, customerId)
                .stream()
                .map(ActivityDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activities);
    }

    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@Valid @RequestBody Activity activity) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        activity.setUser(user);
        Activity saved = activityRepository.save(activity);
        return ResponseEntity.ok(new ActivityDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivity(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return activityRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(userId))
                .map(activity -> ResponseEntity.ok(new ActivityDTO(activity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return activityRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(userId))
                .<ResponseEntity<Void>>map(activity -> {
                    activityRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
