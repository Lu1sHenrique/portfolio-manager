package com.portfolio.dto.response;

import com.portfolio.enums.ProjectStatus;
import com.portfolio.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
    private BigDecimal totalBudget;
    private String description;
    private MemberResponse manager;
    private ProjectStatus status;
    private RiskLevel riskLevel;
    private List<MemberResponse> allocatedMembers;
}
