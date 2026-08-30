package com.crm.backend.company;

import jakarta.validation.constraints.NotBlank;

public class CompanyDTO {

    private Long id;

    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Industry is required")
    private String industry;

    public CompanyDTO() {
    }

    public CompanyDTO(Long id, String name, String industry) {
        this.id = id;
        this.name = name;
        this.industry = industry;
    }

    public static CompanyDTO fromEntity(Company company) {
        return new CompanyDTO(company.getId(), company.getName(), company.getIndustry());
    }

    public static Company toEntity(CompanyDTO dto) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setIndustry(dto.getIndustry());
        return company;
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

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
