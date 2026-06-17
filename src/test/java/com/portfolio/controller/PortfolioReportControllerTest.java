package com.portfolio.controller;

import com.portfolio.dto.response.PortfolioReportResponse;
import com.portfolio.security.JwtService;
import com.portfolio.service.PortfolioReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioReportController.class)
class PortfolioReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioReportService reportService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("GET /api/portfolio/report - Should return portfolio report")
    void shouldReturnPortfolioReport() throws Exception {
        UserDetails userDetails = User.builder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();

        PortfolioReportResponse report = PortfolioReportResponse.builder()
                .projectCountByStatus(Map.of("IN_ANALYSIS", 5L, "CLOSED", 3L))
                .totalBudgetByStatus(Map.of("IN_ANALYSIS", BigDecimal.valueOf(500000), "CLOSED", BigDecimal.valueOf(300000)))
                .averageClosedProjectDurationDays(45.5)
                .totalUniqueAllocatedMembers(10L)
                .build();

        when(reportService.generateReport()).thenReturn(report);

        mockMvc.perform(get("/api/portfolio/report")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCountByStatus.IN_ANALYSIS").value(5))
                .andExpect(jsonPath("$.projectCountByStatus.CLOSED").value(3))
                .andExpect(jsonPath("$.totalBudgetByStatus.IN_ANALYSIS").value(500000))
                .andExpect(jsonPath("$.averageClosedProjectDurationDays").value(45.5))
                .andExpect(jsonPath("$.totalUniqueAllocatedMembers").value(10));
    }

    @Test
    @DisplayName("GET /api/portfolio/report - Should require authentication")
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/portfolio/report"))
                .andExpect(status().isUnauthorized());
    }
}
