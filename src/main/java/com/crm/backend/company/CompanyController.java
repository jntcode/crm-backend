package com.crm.backend.company;

import com.crm.backend.auth.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    private Long getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.findAll(getCurrentUserId()));
    }

    @PostMapping("/companies")
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO dto) {
        return ResponseEntity.ok(companyService.create(dto, getCurrentUserId()));
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyDTO dto) {
        return companyService.update(id, dto, getCurrentUserId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        if (companyService.delete(id, getCurrentUserId())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
