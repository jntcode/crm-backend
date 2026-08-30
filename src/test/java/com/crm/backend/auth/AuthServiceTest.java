package com.crm.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.crm.backend.security.JwtAuthenticationFilter;
import com.crm.backend.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, AuthService.class, JwtService.class, UserService.class})
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void shouldRegisterUserAndGenerateJwt() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret123");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
    }
}
