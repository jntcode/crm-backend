package com.crm.backend.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that one user can never read, update or delete another user's
 * customers: everything is scoped by owner and missing-for-you resources
 * answer 404 (not 403) so IDs are not leaked.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CustomerIsolationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String registerAndGetToken(String label) throws Exception {
        String email = "iso-" + label + "-" + System.nanoTime() + "@example.com";
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User %s","email":"%s","password":"password123"}
                                """.formatted(label, email)))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return body.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
    }

    private String createCustomer(String token, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/customers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","email":"%s@example.com","phone":"+1 555 000 1111"}
                                """.formatted(name, name.toLowerCase().replace(' ', '-'))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    @Test
    void userBCannotSeeOrUpdateOrDeleteUserAsCustomer() throws Exception {
        String tokenA = registerAndGetToken("a");
        String tokenB = registerAndGetToken("b");

        String created = createCustomer(tokenA, "A Customer");
        long id = Long.parseLong(created.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        // B's list never contains A's customer.
        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").doesNotExist());

        // Direct access answers 404, as if the resource does not exist for B.
        mockMvc.perform(get("/api/customers/" + id)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/customers/" + id)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Hijacked\",\"email\":\"hijack@example.com\",\"phone\":\"+1 555\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/customers/" + id)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        // A's customer is untouched and still readable by A.
        mockMvc.perform(get("/api/customers/" + id)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A Customer"));
    }
}
