package com.crm.backend.lead;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    public LeadService(LeadRepository leadRepository, UserRepository userRepository) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }

    public List<LeadDTO> findAll(Long userId) {
        return leadRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(LeadDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public LeadDTO create(LeadDTO dto, Long userId) {
        Lead lead = LeadDTO.toEntity(dto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        lead.setUser(user);
        return LeadDTO.fromEntity(leadRepository.save(lead));
    }

    public Optional<LeadDTO> update(Long id, LeadDTO dto, Long userId) {
        return leadRepository.findById(id).filter(lead -> lead.getUser().getId().equals(userId)).map(lead -> {
            lead.setName(dto.getName());
            lead.setCompany(dto.getCompany());
            lead.setValue(dto.getValue());
            lead.setStatus(dto.getStatus() != null ? dto.getStatus() : lead.getStatus());
            return LeadDTO.fromEntity(leadRepository.save(lead));
        });
    }

    public boolean delete(Long id, Long userId) {
        Optional<Lead> lead = leadRepository.findById(id);
        if (lead.isPresent() && lead.get().getUser().getId().equals(userId)) {
            leadRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long countByUserIdAndStatus(Long userId, LeadStatus status) {
        return leadRepository.countByUserIdAndStatus(userId, status);
    }
}
