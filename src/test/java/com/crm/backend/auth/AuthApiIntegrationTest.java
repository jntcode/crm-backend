package com.crm.backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String register(String name, String email, String password) throws Exception {
        String body = """
                {"name":"%s","email":"%s","password":"%s"}
                """.formatted(name, email, password);
        return mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
    }

    private String extractToken(String responseBody) {
        return responseBody.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
    }

    @Test
    void registerAndLoginWithValidCredentials() throws Exception {
        String email = "auth-ok-" + System.nanoTime() + "@example.com";
        String body = """
                {"name":"Auth Ok","email":"%s","password":"password123"}
                """.formatted(email);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void duplicateRegisterReturns409() throws Exception {
        String email = "auth-dup-" + System.nanoTime() + "@example.com";
        register("Dup", email, "password123");

        String body = """
                {"name":"Dup Again","email":"%s","password":"password123"}
                """.formatted(email);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already registered"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        String email = "auth-wrongpw-" + System.nanoTime() + "@example.com";
        register("Wrong PW", email, "password123");

        String body = """
                {"name":"Wrong PW","email":"%s","password":"not-the-password"}
                """.formatted(email);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerWithBlankEmailReturns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"No Email\",\"email\":\"\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    @Test
    void protectedEndpointWithGarbageTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer not.a.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithValidTokenReturns200() throws Exception {
        String email = "auth-token-" + System.nanoTime() + "@example.com";
        String response = register("Token User", email, "password123");
        String token = extractToken(response);

        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
