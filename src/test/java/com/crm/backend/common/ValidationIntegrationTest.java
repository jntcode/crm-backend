package com.crm.backend.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Malformed or incomplete payloads must produce a 400 with a helpful JSON
 * body — not a 500 with a stack trace.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String token() throws Exception {
        String email = "val-" + System.nanoTime() + "@example.com";
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Validator","email":"%s","password":"password123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString()
                .replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
    }

    @Test
    void createEmailWithoutSubjectReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/emails")
                        .header("Authorization", "Bearer " + token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"\",\"subject\":\"\",\"fromAddress\":\"a@b.com\",\"toAddress\":\"c@d.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.subject").value("Subject is required"));
    }

    @Test
    void createTaskWithoutTitleReturns400() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"customerId\":\"cust-1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.title").value("Title is required"));
    }

    @Test
    void createActivityWithBadTypeReturns400() throws Exception {
        mockMvc.perform(post("/api/activities")
                        .header("Authorization", "Bearer " + token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Call\",\"customerId\":\"cust-1\",\"type\":\"NOT_A_TYPE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid value for 'type'"));
    }

    @Test
    void createCustomerWithoutRequiredFieldsReturns400() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .header("Authorization", "Bearer " + token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.phone").exists());
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/emails")
                        .header("Authorization", "Bearer " + token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed request body"));
    }
}
