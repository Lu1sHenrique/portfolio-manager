package com.portfolio.dto.request;

import com.portfolio.enums.ProjectStatus;
import com.portfolio.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterRequest {

    private String name;
    private ProjectStatus status;
    private RiskLevel riskLevel;
    private Long managerId;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
}
