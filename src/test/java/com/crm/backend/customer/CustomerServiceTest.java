package com.crm.backend.customer;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        userRepository.deleteAll();
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        testUserId = userRepository.save(user).getId();
    }

    @Test
    void shouldCreateAndFetchCustomer() {
        CustomerDTO created = customerService.createCustomer(new CustomerDTO(
                null,
                "Jane Smith",
                "jane@acme.com",
                "+1 555 123 4567",
                null,
                null,
                "Sales Manager",
                "Active",
                "Interested in onboarding",
                null
        ), testUserId);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Jane Smith");
        assertThat(customerService.findAll(testUserId)).hasSize(1);
    }
}
