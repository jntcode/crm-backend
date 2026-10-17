package com.crm.backend.lead;

import com.crm.backend.auth.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    private Long getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @GetMapping("/leads")
    public ResponseEntity<List<LeadDTO>> getAllLeads() {
        return ResponseEntity.ok(leadService.findAll(getCurrentUserId()));
    }

    @PostMapping("/leads")
    public ResponseEntity<LeadDTO> createLead(@Valid @RequestBody LeadDTO dto) {
        return ResponseEntity.ok(leadService.create(dto, getCurrentUserId()));
    }

    @PutMapping("/leads/{id}")
    public ResponseEntity<LeadDTO> updateLead(@PathVariable Long id, @Valid @RequestBody LeadDTO dto) {
        return leadService.update(id, dto, getCurrentUserId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/leads/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        if (leadService.delete(id, getCurrentUserId())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
