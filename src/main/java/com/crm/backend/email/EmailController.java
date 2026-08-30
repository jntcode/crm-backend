package com.crm.backend.email;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "*")
public class EmailController {
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public EmailController(EmailRepository emailRepository, UserRepository userRepository) {
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<EmailDTO>> getAllEmails() {
        Long userId = getCurrentUserId();
        List<EmailDTO> emails = emailRepository.findByUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(EmailDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<EmailDTO>> getEmailsByCustomer(@PathVariable String customerId) {
        Long userId = getCurrentUserId();
        List<EmailDTO> emails = emailRepository.findByUserIdAndCustomerIdOrderBySentAtDesc(userId, customerId)
                .stream()
                .map(EmailDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmailDTO>> getEmailsByStatus(@PathVariable EmailStatus status) {
        Long userId = getCurrentUserId();
        List<EmailDTO> emails = emailRepository.findByUserIdAndStatusOrderBySentAtDesc(userId, status)
                .stream()
                .map(EmailDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emails);
    }

    @PostMapping
    public ResponseEntity<EmailDTO> createEmail(@RequestBody Email email) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        email.setUser(user);
        Email saved = emailRepository.save(email);
        return ResponseEntity.ok(new EmailDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailDTO> getEmail(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return emailRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(userId))
                .map(email -> ResponseEntity.ok(new EmailDTO(email)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailDTO> updateEmail(@PathVariable Long id, @RequestBody Email emailDetails) {
        Long userId = getCurrentUserId();
        return emailRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(userId))
                .map(email -> {
                    email.setStatus(emailDetails.getStatus());
                    email.setBody(emailDetails.getBody());
                    Email updated = emailRepository.save(email);
                    return ResponseEntity.ok(new EmailDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return emailRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(userId))
                .<ResponseEntity<Void>>map(email -> {
                    emailRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
