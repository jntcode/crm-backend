package com.crm.backend.customer;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import com.crm.backend.company.Company;
import com.crm.backend.company.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository, CompanyRepository companyRepository,
                           UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public List<CustomerDTO> findAll(Long userId) {
        return customerRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<CustomerDTO> findById(Long id, Long userId) {
        return customerRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(userId))
                .map(CustomerDTO::fromEntity);
    }

    public CustomerDTO createCustomer(CustomerDTO dto, Long userId) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setPosition(dto.getPosition());
        customer.setStatus(dto.getStatus());
        customer.setNotes(dto.getNotes());
        customer.setCompanyName(dto.getCompanyName());

        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .filter(c -> c.getUser().getId().equals(userId))
                    .orElseThrow(() -> new EntityNotFoundException("Company not found"));
            customer.setCompany(company);
            customer.setCompanyName(company.getName());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        customer.setUser(user);

        Customer saved = customerRepository.save(customer);
        return CustomerDTO.fromEntity(saved);
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO dto, Long userId) {
        Customer customer = customerRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setPosition(dto.getPosition());
        customer.setStatus(dto.getStatus());
        customer.setNotes(dto.getNotes());
        customer.setCompanyName(dto.getCompanyName());

        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .filter(c -> c.getUser().getId().equals(userId))
                    .orElseThrow(() -> new EntityNotFoundException("Company not found"));
            customer.setCompany(company);
            customer.setCompanyName(company.getName());
        } else {
            customer.setCompany(null);
            customer.setCompanyName(dto.getCompanyName());
        }

        return CustomerDTO.fromEntity(customerRepository.save(customer));
    }

    public void deleteCustomer(Long id, Long userId) {
        Customer customer = customerRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        customerRepository.deleteById(id);
    }
}
