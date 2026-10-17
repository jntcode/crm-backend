package com.crm.backend.task;

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
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        Long userId = getCurrentUserId();
        List<TaskDTO> tasks = taskRepository.findByUserIdOrderByDueDateAsc(userId)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TaskDTO>> getTasksByCustomer(@PathVariable String customerId) {
        Long userId = getCurrentUserId();
        List<TaskDTO> tasks = taskRepository.findByUserIdAndCustomerIdOrderByDueDateAsc(userId, customerId)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable TaskStatus status) {
        Long userId = getCurrentUserId();
        List<TaskDTO> tasks = taskRepository.findByUserIdAndStatusOrderByDueDateAsc(userId, status)
                .stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody Task task) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        task.setUser(user);
        Task saved = taskRepository.save(task);
        return ResponseEntity.ok(new TaskDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(userId))
                .map(task -> ResponseEntity.ok(new TaskDTO(task)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        Long userId = getCurrentUserId();
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(userId))
                .map(task -> {
                    task.setTitle(taskDetails.getTitle());
                    task.setDescription(taskDetails.getDescription());
                    task.setStatus(taskDetails.getStatus());
                    task.setPriority(taskDetails.getPriority());
                    task.setDueDate(taskDetails.getDueDate());
                    task.setAssignedTo(taskDetails.getAssignedTo());
                    Task updated = taskRepository.save(task);
                    return ResponseEntity.ok(new TaskDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(userId))
                .<ResponseEntity<Void>>map(task -> {
                    taskRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
