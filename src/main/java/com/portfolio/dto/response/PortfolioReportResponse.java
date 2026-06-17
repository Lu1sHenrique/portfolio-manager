package com.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioReportResponse {

    private Map<String, Long> projectCountByStatus;
    private Map<String, BigDecimal> totalBudgetByStatus;
    private Double averageClosedProjectDurationDays;
    private Long totalUniqueAllocatedMembers;
}
