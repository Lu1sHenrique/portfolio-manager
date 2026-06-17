package com.portfolio.model.entity;

import com.portfolio.model.enums.RiskLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {

    @Nested
    @DisplayName("Risk Level Calculation Tests")
    class RiskLevelTests {

        @Test
        @DisplayName("Low risk: budget up to 100000 AND deadline <= 3 months")
        void lowRiskWhenBudgetUpTo100000AndDeadlineUpTo3Months() {
            Project project = createProject(new BigDecimal("100000"), 3);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        }

        @Test
        @DisplayName("Low risk: budget 50000 AND deadline 2 months")
        void lowRiskWhenBudget50000AndDeadline2Months() {
            Project project = createProject(new BigDecimal("50000"), 2);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        }

        @Test
        @DisplayName("Low risk: budget exactly 100000 AND deadline exactly 3 months")
        void lowRiskWhenBudgetExactly100000AndDeadlineExactly3Months() {
            Project project = createProject(new BigDecimal("100000"), 3);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        }

        @Test
        @DisplayName("Medium risk: budget between 100001 and 500000")
        void mediumRiskWhenBudgetBetween100001And500000() {
            Project project = createProject(new BigDecimal("250000"), 2);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }

        @Test
        @DisplayName("Medium risk: budget exactly 100001")
        void mediumRiskWhenBudgetExactly100001() {
            Project project = createProject(new BigDecimal("100001"), 2);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }

        @Test
        @DisplayName("Medium risk: budget exactly 500000")
        void mediumRiskWhenBudgetExactly500000() {
            Project project = createProject(new BigDecimal("500000"), 2);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }

        @Test
        @DisplayName("Medium risk: deadline between 3 and 6 months with low budget")
        void mediumRiskWhenDeadlineBetween3And6MonthsWithLowBudget() {
            Project project = createProject(new BigDecimal("50000"), 5);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }

        @Test
        @DisplayName("Medium risk: deadline exactly 4 months")
        void mediumRiskWhenDeadlineExactly4Months() {
            Project project = createProject(new BigDecimal("50000"), 4);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }

        @Test
        @DisplayName("Medium risk: deadline exactly 6 months")
        void mediumRiskWhenDeadlineExactly6Months() {
            Project project = createProject(new BigDecimal("50000"), 6);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }

        @Test
        @DisplayName("High risk: budget above 500000")
        void highRiskWhenBudgetAbove500000() {
            Project project = createProject(new BigDecimal("500001"), 2);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
        }

        @Test
        @DisplayName("High risk: budget 1000000")
        void highRiskWhenBudget1000000() {
            Project project = createProject(new BigDecimal("1000000"), 2);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
        }

        @Test
        @DisplayName("High risk: deadline above 6 months")
        void highRiskWhenDeadlineAbove6Months() {
            Project project = createProject(new BigDecimal("50000"), 7);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
        }

        @Test
        @DisplayName("High risk: deadline 12 months")
        void highRiskWhenDeadline12Months() {
            Project project = createProject(new BigDecimal("50000"), 12);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
        }

        @Test
        @DisplayName("High risk: both budget above 500000 and deadline above 6 months")
        void highRiskWhenBothConditionsMet() {
            Project project = createProject(new BigDecimal("600000"), 8);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
        }

        @Test
        @DisplayName("Medium risk: low budget but deadline 3-6 months takes precedence over low risk conditions")
        void mediumRiskPrecedenceOverLowWhenDeadlineInRange() {
            Project project = createProject(new BigDecimal("80000"), 5);
            assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }

        private Project createProject(BigDecimal budget, int months) {
            LocalDate startDate = LocalDate.now();
            LocalDate expectedEndDate = startDate.plusMonths(months);

            return Project.builder()
                    .totalBudget(budget)
                    .startDate(startDate)
                    .expectedEndDate(expectedEndDate)
                    .build();
        }
    }

    @Nested
    @DisplayName("Duration Calculation Tests")
    class DurationTests {

        @Test
        @DisplayName("Calculate actual duration when actual end date is set")
        void calculateActualDurationWithEndDate() {
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate actualEndDate = LocalDate.of(2024, 1, 31);

            Project project = Project.builder()
                    .startDate(startDate)
                    .expectedEndDate(startDate.plusMonths(2))
                    .actualEndDate(actualEndDate)
                    .totalBudget(BigDecimal.valueOf(100000))
                    .build();

            assertThat(project.getActualDurationInDays()).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("Member Management Tests")
    class MemberManagementTests {

        @Test
        @DisplayName("Add member to project")
        void addMemberToProject() {
            Project project = createBasicProject();
            Member member = createMember();
            ProjectMember projectMember = ProjectMember.builder()
                    .member(member)
                    .build();

            project.addMember(projectMember);

            assertThat(project.getMembers()).hasSize(1);
            assertThat(projectMember.getProject()).isEqualTo(project);
        }

        @Test
        @DisplayName("Remove member from project")
        void removeMemberFromProject() {
            Project project = createBasicProject();
            Member member = createMember();
            ProjectMember projectMember = ProjectMember.builder()
                    .member(member)
                    .build();

            project.addMember(projectMember);
            project.removeMember(projectMember);

            assertThat(project.getMembers()).isEmpty();
            assertThat(projectMember.getProject()).isNull();
        }

        private Project createBasicProject() {
            return Project.builder()
                    .name("Test Project")
                    .startDate(LocalDate.now())
                    .expectedEndDate(LocalDate.now().plusMonths(3))
                    .totalBudget(BigDecimal.valueOf(100000))
                    .build();
        }

        private Member createMember() {
            return Member.builder()
                    .id(1L)
                    .name("Test Member")
                    .build();
        }
    }
}
