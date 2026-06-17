package com.portfolio.model.repository;

import com.portfolio.model.entity.Project;
import com.portfolio.model.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    @Query("SELECT p.status, COUNT(p), SUM(p.totalBudget) FROM Project p GROUP BY p.status")
    List<Object[]> getStatusSummary();

    @Query("SELECT AVG(DATEDIFF(day, p.startDate, p.actualEndDate)) FROM Project p WHERE p.status = 'CLOSED' AND p.actualEndDate IS NOT NULL")
    Double getAverageClosedProjectDuration();

    @Query("SELECT COUNT(DISTINCT pm.member.id) FROM ProjectMember pm")
    Long countUniqueAllocatedMembers();

    List<Project> findByStatus(ProjectStatus status);
}
