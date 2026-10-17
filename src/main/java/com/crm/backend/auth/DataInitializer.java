package com.crm.backend.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a local demo account so the dev environment is usable out of the box.
 * Active only in the {@code dev} profile — production must never contain a
 * shared default account.
 */
@Profile("dev")
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@crm.com")) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@crm.com");
            admin.setPassword(passwordEncoder.encode("secret123"));
            userRepository.save(admin);
        }
    }
}
