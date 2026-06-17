package com.portfolio.service;

import com.portfolio.view.response.PortfolioReportResponse;
import com.portfolio.model.entity.Member;
import com.portfolio.model.entity.Project;
import com.portfolio.model.enums.MemberRole;
import com.portfolio.model.enums.ProjectStatus;
import com.portfolio.model.repository.ProjectMemberRepository;
import com.portfolio.model.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioReportServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private PortfolioReportService portfolioReportService;

    @Test
    @DisplayName("Should generate report with project count by status")
    void shouldGenerateReportWithProjectCountByStatus() {
        List<Object[]> statusSummary = new ArrayList<>();
        statusSummary.add(new Object[]{ProjectStatus.IN_ANALYSIS, 5L, BigDecimal.valueOf(100000)});
        statusSummary.add(new Object[]{ProjectStatus.CLOSED, 3L, BigDecimal.valueOf(300000)});

        when(projectRepository.getStatusSummary()).thenReturn(statusSummary);
        when(projectRepository.findByStatus(ProjectStatus.CLOSED)).thenReturn(Collections.emptyList());
        when(projectMemberRepository.count()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.getProjectCountByStatus()).containsEntry("IN_ANALYSIS", 5L);
        assertThat(report.getProjectCountByStatus()).containsEntry("CLOSED", 3L);
    }

    @Test
    @DisplayName("Should generate report with total budget by status")
    void shouldGenerateReportWithTotalBudgetByStatus() {
        List<Object[]> statusSummary = new ArrayList<>();
        statusSummary.add(new Object[]{ProjectStatus.IN_ANALYSIS, 2L, BigDecimal.valueOf(200000)});
        statusSummary.add(new Object[]{ProjectStatus.IN_PROGRESS, 1L, BigDecimal.valueOf(500000)});

        when(projectRepository.getStatusSummary()).thenReturn(statusSummary);
        when(projectRepository.findByStatus(ProjectStatus.CLOSED)).thenReturn(Collections.emptyList());
        when(projectMemberRepository.count()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.getTotalBudgetByStatus()).containsEntry("IN_ANALYSIS", BigDecimal.valueOf(200000));
        assertThat(report.getTotalBudgetByStatus()).containsEntry("IN_PROGRESS", BigDecimal.valueOf(500000));
    }

    @Test
    @DisplayName("Should calculate average duration of closed projects")
    void shouldCalculateAverageDurationOfClosedProjects() {
        Project closedProject1 = Project.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .expectedEndDate(LocalDate.of(2024, 6, 1))
                .actualEndDate(LocalDate.of(2024, 2, 1))
                .totalBudget(BigDecimal.valueOf(100000))
                .status(ProjectStatus.CLOSED)
                .build();

        Project closedProject2 = Project.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .expectedEndDate(LocalDate.of(2024, 6, 1))
                .actualEndDate(LocalDate.of(2024, 3, 1))
                .totalBudget(BigDecimal.valueOf(100000))
                .status(ProjectStatus.CLOSED)
                .build();

        List<Project> closedProjects = List.of(closedProject1, closedProject2);

        when(projectRepository.getStatusSummary()).thenReturn(new ArrayList<>());
        when(projectRepository.findByStatus(ProjectStatus.CLOSED)).thenReturn(closedProjects);
        when(projectMemberRepository.count()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.getAverageClosedProjectDurationDays()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should return zero average when no closed projects")
    void shouldReturnZeroAverageWhenNoClosedProjects() {
        when(projectRepository.getStatusSummary()).thenReturn(new ArrayList<>());
        when(projectRepository.findByStatus(ProjectStatus.CLOSED)).thenReturn(Collections.emptyList());
        when(projectMemberRepository.count()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.getAverageClosedProjectDurationDays()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should count unique allocated members")
    void shouldCountUniqueAllocatedMembers() {
        List<Object[]> statusSummary = new ArrayList<>();
        statusSummary.add(new Object[]{ProjectStatus.IN_ANALYSIS, 2L, BigDecimal.valueOf(200000)});

        when(projectRepository.getStatusSummary()).thenReturn(statusSummary);
        when(projectRepository.findByStatus(ProjectStatus.CLOSED)).thenReturn(Collections.emptyList());
        when(projectMemberRepository.count()).thenReturn(5L);
        when(projectRepository.countUniqueAllocatedMembers()).thenReturn(5L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.getTotalUniqueAllocatedMembers()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should return zero unique members when no allocations")
    void shouldReturnZeroUniqueMembersWhenNoAllocations() {
        when(projectRepository.getStatusSummary()).thenReturn(new ArrayList<>());
        when(projectRepository.findByStatus(ProjectStatus.CLOSED)).thenReturn(Collections.emptyList());
        when(projectMemberRepository.count()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.getTotalUniqueAllocatedMembers()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should handle null budget in status summary")
    void shouldHandleNullBudgetInStatusSummary() {
        List<Object[]> statusSummary = new ArrayList<>();
        statusSummary.add(new Object[]{ProjectStatus.IN_ANALYSIS, 1L, null});

        when(projectRepository.getStatusSummary()).thenReturn(statusSummary);
        when(projectRepository.findByStatus(ProjectStatus.CLOSED)).thenReturn(Collections.emptyList());
        when(projectMemberRepository.count()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.getTotalBudgetByStatus()).containsEntry("IN_ANALYSIS", BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle closed projects without actual end date")
    void shouldHandleClosedProjectsWithoutActualEndDate() {
        Project closedProject = Project.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .expectedEndDate(LocalDate.of(2024, 6, 1))
                .actualEndDate(null)
                .totalBudget(BigDecimal.valueOf(100000))
                .status(ProjectStatus.CLOSED)
                .build();

        when(projectRepository.getStatusSummary()).thenReturn(new ArrayList<>());
        when(projectRepository.findByStatus(ProjectStatus.CLOSED)).thenReturn(List.of(closedProject));
        when(projectMemberRepository.count()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.getAverageClosedProjectDurationDays()).isEqualTo(0.0);
    }
}
