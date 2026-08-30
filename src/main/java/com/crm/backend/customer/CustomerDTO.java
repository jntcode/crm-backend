package com.crm.backend.customer;

import com.crm.backend.company.Company;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CustomerDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private Long companyId;
    private String companyName;
    private String position;
    private String status;
    private String notes;

    private LocalDateTime createdAt;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String name, String email, String phone, Long companyId, String companyName,
                       String position, String status, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.companyId = companyId;
        this.companyName = companyName;
        this.position = position;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static CustomerDTO fromEntity(Customer customer) {
        Company company = customer.getCompany();
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                company != null ? company.getId() : null,
                customer.getCompanyName() != null ? customer.getCompanyName() : (company != null ? company.getName() : null),
                customer.getPosition(),
                customer.getStatus(),
                customer.getNotes(),
                customer.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
