package com.portfolio.service;

import com.portfolio.view.response.PortfolioReportResponse;
import com.portfolio.model.entity.Project;
import com.portfolio.model.enums.ProjectStatus;
import com.portfolio.model.repository.ProjectMemberRepository;
import com.portfolio.model.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioReportService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional(readOnly = true)
    public PortfolioReportResponse generateReport() {
        Map<String, Long> countByStatus = new HashMap<>();
        Map<String, BigDecimal> budgetByStatus = new HashMap<>();

        List<Object[]> statusSummary = projectRepository.getStatusSummary();
        for (Object[] row : statusSummary) {
            String status = ((ProjectStatus) row[0]).name();
            Long count = (Long) row[1];
            BigDecimal budget = (BigDecimal) row[2];

            countByStatus.put(status, count);
            budgetByStatus.put(status, budget != null ? budget : BigDecimal.ZERO);
        }

        Double averageDuration = calculateAverageClosedProjectDuration();
        Long uniqueMembers = projectMemberRepository.count() > 0
                ? projectRepository.countUniqueAllocatedMembers()
                : 0L;

        return PortfolioReportResponse.builder()
                .projectCountByStatus(countByStatus)
                .totalBudgetByStatus(budgetByStatus)
                .averageClosedProjectDurationDays(averageDuration)
                .totalUniqueAllocatedMembers(uniqueMembers)
                .build();
    }

    private Double calculateAverageClosedProjectDuration() {
        List<Project> closedProjects = projectRepository.findByStatus(ProjectStatus.CLOSED);
        if (closedProjects.isEmpty()) {
            return 0.0;
        }

        double totalDays = closedProjects.stream()
                .filter(p -> p.getActualEndDate() != null)
                .mapToLong(Project::getActualDurationInDays)
                .sum();

        long count = closedProjects.stream()
                .filter(p -> p.getActualEndDate() != null)
                .count();

        return count > 0 ? totalDays / count : 0.0;
    }
}
