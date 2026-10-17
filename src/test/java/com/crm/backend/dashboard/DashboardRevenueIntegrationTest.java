package com.crm.backend.dashboard;

import com.crm.backend.auth.User;
import com.crm.backend.auth.UserRepository;
import com.crm.backend.lead.Lead;
import com.crm.backend.lead.LeadRepository;
import com.crm.backend.lead.LeadStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Revenue must be computed from real WON leads owned by the caller — never a
 * hardcoded value — and must never include another user's pipeline.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DashboardRevenueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeadRepository leadRepository;

    private String register(String label) throws Exception {
        String email = "rev-" + label + "-" + System.nanoTime() + "@example.com";
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User %s","email":"%s","password":"password123"}
                                """.formatted(label, email)))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    private String tokenOf(String registerResponseBody) {
        return registerResponseBody.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
    }

    private User userOf(String registerResponseBody) {
        String email = registerResponseBody.replaceAll(".*\"email\"\\s*:\\s*\"([^\"]+)\".*", "$1");
        return userRepository.findByEmail(email).orElseThrow();
    }

    private void createLeadViaApi(String token, String name, String status, int value) throws Exception {
        mockMvc.perform(post("/api/leads")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","company":"Acme","value":%d,"status":"%s"}
                                """.formatted(name, value, status)))
                .andExpect(status().isOk());
    }

    @Test
    void monthlyRevenueAndChartUseRealWonLeadsPerUser() throws Exception {
        String userAJson = register("a");
        String tokenA = tokenOf(userAJson);
        User userA = userOf(userAJson);

        // Current month: WON 100 + NEW 50 (NEW must not count).
        createLeadViaApi(tokenA, "Won This Month", "WON", 100);
        createLeadViaApi(tokenA, "Open Deal", "NEW", 50);

        // Previous month: WON 200 inserted directly with a back-dated created_at.
        Lead lastMonth = new Lead();
        lastMonth.setName("Won Last Month");
        lastMonth.setCompany("Old Co");
        lastMonth.setValue(new BigDecimal("200"));
        lastMonth.setStatus(LeadStatus.WON);
        lastMonth.setUser(userA);
        lastMonth.setCreatedAt(YearMonth.now().minusMonths(1).atDay(1).atStartOfDay());
        leadRepository.save(lastMonth);

        // Another user's big WON lead must never leak into A's numbers.
        String tokenB = tokenOf(register("b"));
        createLeadViaApi(tokenB, "B Big Win", "WON", 999);

        mockMvc.perform(get("/api/dashboard/stats")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyRevenue").value(100.0))
                .andExpect(jsonPath("$.totalDeals").value(3));

        mockMvc.perform(get("/api/dashboard/revenue")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[5].month").value(YearMonth.now().toString()))
                .andExpect(jsonPath("$[5].revenue").value(100.0))
                .andExpect(jsonPath("$[4].month").value(YearMonth.now().minusMonths(1).toString()))
                .andExpect(jsonPath("$[4].revenue").value(200.0))
                .andExpect(jsonPath("$[0].revenue").value(0.0));

        // B sees only their own 999 this month.
        mockMvc.perform(get("/api/dashboard/revenue")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[5].revenue").value(999.0))
                .andExpect(jsonPath("$[4].revenue").value(0.0));
    }

    @Test
    void revenueIsEmptyAndZeroForNewUser() throws Exception {
        String token = tokenOf(register("empty"));

        mockMvc.perform(get("/api/dashboard/revenue")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[5].revenue").value(0.0));

        mockMvc.perform(get("/api/dashboard/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyRevenue").value(0.0));
    }

    @Test
    void revenueEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/dashboard/revenue"))
                .andExpect(status().isUnauthorized());
    }
}
