package com.portfolio.view.request;

import com.portfolio.model.enums.ProjectStatus;
import com.portfolio.model.enums.RiskLevel;
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
