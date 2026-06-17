package com.portfolio.service;

import com.portfolio.dto.request.ProjectFilterRequest;
import com.portfolio.dto.request.ProjectRequest;
import com.portfolio.dto.response.ProjectResponse;
import com.portfolio.entity.Member;
import com.portfolio.entity.Project;
import com.portfolio.entity.ProjectMember;
import com.portfolio.enums.ProjectStatus;
import com.portfolio.exception.BusinessRuleException;
import com.portfolio.exception.InvalidStatusTransitionException;
import com.portfolio.exception.MemberAllocationException;
import com.portfolio.exception.ProjectDeletionException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.mapper.ProjectMapper;
import com.portfolio.repository.ProjectMemberRepository;
import com.portfolio.repository.ProjectRepository;
import com.portfolio.specification.ProjectSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final int MAX_MEMBERS_PER_PROJECT = 10;
    private static final int MAX_ACTIVE_PROJECTS_PER_MEMBER = 3;

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberService memberService;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public Page<ProjectResponse> findAll(ProjectFilterRequest filter, Pageable pageable) {
        return projectRepository.findAll(ProjectSpecification.withFilters(filter), pageable)
                .map(projectMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        Project project = getProjectOrThrow(id);
        return projectMapper.toResponse(project);
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        validateDates(request.getStartDate(), request.getExpectedEndDate());

        Member manager = memberService.findById(request.getManagerId());
        Project project = projectMapper.toEntity(request);
        project.setManager(manager);
        project.setStatus(ProjectStatus.IN_ANALYSIS);

        Project saved = projectRepository.save(project);
        return projectMapper.toResponse(saved);
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getProjectOrThrow(id);
        validateDates(request.getStartDate(), request.getExpectedEndDate());

        Member manager = memberService.findById(request.getManagerId());
        projectMapper.updateEntity(request, project);
        project.setManager(manager);

        Project saved = projectRepository.save(project);
        return projectMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Project project = getProjectOrThrow(id);

        if (!project.getStatus().isDeletable()) {
            throw new ProjectDeletionException(project.getStatus());
        }

        projectRepository.delete(project);
    }

    @Transactional
    public ProjectResponse updateStatus(Long id, ProjectStatus newStatus) {
        Project project = getProjectOrThrow(id);
        ProjectStatus currentStatus = project.getStatus();

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        project.setStatus(newStatus);

        if (newStatus == ProjectStatus.CLOSED) {
            project.setActualEndDate(LocalDate.now());
        }

        Project saved = projectRepository.save(project);
        return projectMapper.toResponse(saved);
    }

    @Transactional
    public ProjectResponse allocateMember(Long projectId, Long memberId) {
        Project project = getProjectOrThrow(projectId);
        Member member = memberService.findById(memberId);

        validateMemberAllocation(project, member);

        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .member(member)
                .allocationDate(LocalDate.now())
                .build();

        project.addMember(projectMember);
        Project saved = projectRepository.save(project);
        return projectMapper.toResponse(saved);
    }

    @Transactional
    public ProjectResponse deallocateMember(Long projectId, Long memberId) {
        Project project = getProjectOrThrow(projectId);

        ProjectMember projectMember = projectMemberRepository
                .findByProjectIdAndMemberId(projectId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member allocation not found for project " + projectId + " and member " + memberId));

        validateMinimumMembers(project);

        project.removeMember(projectMember);
        projectMemberRepository.delete(projectMember);

        return projectMapper.toResponse(project);
    }

    private Project getProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    private void validateDates(LocalDate startDate, LocalDate expectedEndDate) {
        if (expectedEndDate.isBefore(startDate)) {
            throw new BusinessRuleException("Expected end date must be after start date");
        }
    }

    private void validateMemberAllocation(Project project, Member member) {
        if (!member.isEmployee()) {
            throw new MemberAllocationException("Only members with role EMPLOYEE can be allocated to projects");
        }

        if (project.getMembers().size() >= MAX_MEMBERS_PER_PROJECT) {
            throw new MemberAllocationException(
                    "Project has reached maximum number of members (" + MAX_MEMBERS_PER_PROJECT + ")");
        }

        if (projectMemberRepository.existsByProjectIdAndMemberId(project.getId(), member.getId())) {
            throw new MemberAllocationException("Member is already allocated to this project");
        }

        int activeProjects = projectMemberRepository.countActiveProjectsByMember(member.getId());
        if (activeProjects >= MAX_ACTIVE_PROJECTS_PER_MEMBER) {
            throw new MemberAllocationException(
                    "Member has reached maximum number of active projects (" + MAX_ACTIVE_PROJECTS_PER_MEMBER + ")");
        }
    }

    private void validateMinimumMembers(Project project) {
        if (project.getMembers().size() <= 1 && project.getStatus().isActiveForAllocation()) {
            throw new MemberAllocationException("Project must have at least one member while active");
        }
    }
}
