package com.crm.backend.auth;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String VALID_SECRET = "unit-test-secret-that-is-long-enough-32chars";

    private JwtService serviceWithSecret(String secret) {
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "secretKey", secret);
        ReflectionTestUtils.setField(service, "expiration", 86400000L);
        service.init();
        return service;
    }

    private User user(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("Test User");
        return user;
    }

    @Test
    void generatesAndParsesToken() {
        JwtService service = serviceWithSecret(VALID_SECRET);
        User user = user("round@trip.com");

        String token = service.generateToken(user);

        assertThat(service.extractUsername(token)).isEqualTo("round@trip.com");
        assertThat(service.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValidRejectsDifferentUser() {
        JwtService service = serviceWithSecret(VALID_SECRET);
        String token = service.generateToken(user("alice@example.com"));

        assertThat(service.isTokenValid(token, user("bob@example.com"))).isFalse();
    }

    @Test
    void rejectsBlankSecretAtStartup() {
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "secretKey", "  ");
        ReflectionTestUtils.setField(service, "expiration", 86400000L);

        assertThatThrownBy(service::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT secret is not configured");
    }

    @Test
    void rejectsShortSecretAtStartup() {
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "secretKey", "short");
        ReflectionTestUtils.setField(service, "expiration", 86400000L);

        assertThatThrownBy(service::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("too short");
    }

    @Test
    void rejectsTamperedToken() {
        JwtService service = serviceWithSecret(VALID_SECRET);
        String token = service.generateToken(user("alice@example.com"));
        String tampered = token.substring(0, token.length() - 2) + "xy";

        assertThatThrownBy(() -> service.extractUsername(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsGarbageToken() {
        JwtService service = serviceWithSecret(VALID_SECRET);

        assertThatThrownBy(() -> service.extractUsername("not-a-jwt"))
                .isInstanceOf(JwtException.class);
    }
}
