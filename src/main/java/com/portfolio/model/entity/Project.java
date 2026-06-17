package com.portfolio.model.entity;

import com.portfolio.model.enums.ProjectStatus;
import com.portfolio.model.enums.RiskLevel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private static final BigDecimal LOW_RISK_BUDGET_THRESHOLD = new BigDecimal("100000");
    private static final BigDecimal MEDIUM_RISK_BUDGET_THRESHOLD = new BigDecimal("500000");
    private static final long LOW_RISK_MONTHS_THRESHOLD = 3;
    private static final long MEDIUM_RISK_MONTHS_THRESHOLD = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "expected_end_date", nullable = false)
    private LocalDate expectedEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "total_budget", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalBudget;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Member manager;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.IN_ANALYSIS;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectMember> members = new ArrayList<>();

    @Transient
    public RiskLevel getRiskLevel() {
        long months = calculateDurationInMonths();

        boolean isHighBudget = totalBudget.compareTo(MEDIUM_RISK_BUDGET_THRESHOLD) > 0;
        boolean isLongDuration = months > MEDIUM_RISK_MONTHS_THRESHOLD;

        if (isHighBudget || isLongDuration) {
            return RiskLevel.HIGH;
        }

        boolean isLowBudget = totalBudget.compareTo(LOW_RISK_BUDGET_THRESHOLD) <= 0;
        boolean isShortDuration = months <= LOW_RISK_MONTHS_THRESHOLD;

        if (isLowBudget && isShortDuration) {
            return RiskLevel.LOW;
        }

        return RiskLevel.MEDIUM;
    }

    private long calculateDurationInMonths() {
        return ChronoUnit.MONTHS.between(startDate, expectedEndDate);
    }

    public long getActualDurationInDays() {
        if (actualEndDate == null) {
            return ChronoUnit.DAYS.between(startDate, LocalDate.now());
        }
        return ChronoUnit.DAYS.between(startDate, actualEndDate);
    }

    public void addMember(ProjectMember projectMember) {
        members.add(projectMember);
        projectMember.setProject(this);
    }

    public void removeMember(ProjectMember projectMember) {
        members.remove(projectMember);
        projectMember.setProject(null);
    }
}
