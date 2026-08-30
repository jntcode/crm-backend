package com.crm.backend.lead;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LeadDTO {

    private Long id;

    @NotBlank(message = "Lead name is required")
    private String name;

    @NotBlank(message = "Company is required")
    private String company;

    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Value must be positive")
    private BigDecimal value;

    private LeadStatus status = LeadStatus.NEW;

    public LeadDTO() {
    }

    public LeadDTO(Long id, String name, String company, BigDecimal value, LeadStatus status) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.value = value;
        this.status = status;
    }

    public static LeadDTO fromEntity(Lead lead) {
        return new LeadDTO(
                lead.getId(),
                lead.getName(),
                lead.getCompany(),
                lead.getValue(),
                lead.getStatus()
        );
    }

    public static Lead toEntity(LeadDTO dto) {
        Lead lead = new Lead();
        lead.setName(dto.getName());
        lead.setCompany(dto.getCompany());
        lead.setValue(dto.getValue());
        lead.setStatus(dto.getStatus() != null ? dto.getStatus() : LeadStatus.NEW);
        return lead;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }
}
