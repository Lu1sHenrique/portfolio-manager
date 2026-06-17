package com.portfolio.repository;

import com.portfolio.entity.ProjectMember;
import com.portfolio.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @Query("SELECT COUNT(pm) FROM ProjectMember pm WHERE pm.member.id = :memberId " +
            "AND pm.project.status NOT IN ('CLOSED', 'CANCELLED')")
    int countActiveProjectsByMember(@Param("memberId") Long memberId);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.member.id = :memberId")
    Optional<ProjectMember> findByProjectIdAndMemberId(@Param("projectId") Long projectId,
                                                        @Param("memberId") Long memberId);

    List<ProjectMember> findByProjectId(Long projectId);

    boolean existsByProjectIdAndMemberId(Long projectId, Long memberId);
}
