package com.crm.backend.customer;

import com.crm.backend.auth.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    private Long getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAll(getCurrentUserId()));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        return customerService.findById(id, getCurrentUserId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.createCustomer(dto, getCurrentUserId()));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto, getCurrentUserId()));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
