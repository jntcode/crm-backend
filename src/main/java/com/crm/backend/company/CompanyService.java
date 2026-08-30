package com.crm.backend.company;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public List<CompanyDTO> findAll(Long userId) {
        return companyRepository.findByUserIdOrderByNameAsc(userId)
                .stream()
                .map(CompanyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CompanyDTO create(CompanyDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (companyRepository.findByNameAndUserId(dto.getName(), userId).isPresent()) {
            throw new IllegalArgumentException("Company with this name already exists for this user");
        }

        Company company = CompanyDTO.toEntity(dto);
        company.setUser(user);
        return CompanyDTO.fromEntity(companyRepository.save(company));
    }

    public Optional<CompanyDTO> update(Long id, CompanyDTO dto, Long userId) {
        return companyRepository.findById(id).filter(c -> c.getUser().getId().equals(userId)).map(company -> {
            company.setName(dto.getName());
            company.setIndustry(dto.getIndustry());
            return CompanyDTO.fromEntity(companyRepository.save(company));
        });
    }

    public boolean delete(Long id, Long userId) {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent() && company.get().getUser().getId().equals(userId)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
