package com.portfolio.service;

import com.portfolio.view.request.ProjectFilterRequest;
import com.portfolio.view.request.ProjectRequest;
import com.portfolio.view.response.ProjectResponse;
import com.portfolio.model.entity.Member;
import com.portfolio.model.entity.Project;
import com.portfolio.model.entity.ProjectMember;
import com.portfolio.model.enums.MemberRole;
import com.portfolio.model.enums.ProjectStatus;
import com.portfolio.exception.BusinessRuleException;
import com.portfolio.exception.InvalidStatusTransitionException;
import com.portfolio.exception.MemberAllocationException;
import com.portfolio.exception.ProjectDeletionException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.view.mapper.ProjectMapper;
import com.portfolio.model.repository.ProjectMemberRepository;
import com.portfolio.model.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Member manager;
    private Member employee;
    private Project project;
    private ProjectRequest projectRequest;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        manager = Member.builder()
                .id(1L)
                .name("Manager")
                .role(MemberRole.MANAGER)
                .build();

        employee = Member.builder()
                .id(2L)
                .name("Employee")
                .role(MemberRole.EMPLOYEE)
                .build();

        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(3))
                .totalBudget(BigDecimal.valueOf(100000))
                .manager(manager)
                .status(ProjectStatus.IN_ANALYSIS)
                .members(new ArrayList<>())
                .build();

        projectRequest = ProjectRequest.builder()
                .name("Test Project")
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(3))
                .totalBudget(BigDecimal.valueOf(100000))
                .managerId(1L)
                .build();

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .name("Test Project")
                .status(ProjectStatus.IN_ANALYSIS)
                .build();
    }

    @Nested
    @DisplayName("Find All Projects Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return paginated projects with filters")
        void shouldReturnPaginatedProjectsWithFilters() {
            ProjectFilterRequest filter = new ProjectFilterRequest();
            Pageable pageable = PageRequest.of(0, 10);
            Page<Project> projectPage = new PageImpl<>(List.of(project));

            when(projectRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(projectPage);
            when(projectMapper.toResponse(any(Project.class))).thenReturn(projectResponse);

            Page<ProjectResponse> result = projectService.findAll(filter, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(projectRepository).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    @DisplayName("Find By ID Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return project when found")
        void shouldReturnProjectWhenFound() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse result = projectService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when project not found")
        void shouldThrowExceptionWhenProjectNotFound() {
            when(projectRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> projectService.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Project not found");
        }
    }

    @Nested
    @DisplayName("Create Project Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create project successfully")
        void shouldCreateProjectSuccessfully() {
            when(memberService.findById(1L)).thenReturn(manager);
            when(projectMapper.toEntity(projectRequest)).thenReturn(project);
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse result = projectService.create(projectRequest);

            assertThat(result).isNotNull();
            verify(projectRepository).save(any(Project.class));
        }

        @Test
        @DisplayName("Should throw exception when end date is before start date")
        void shouldThrowExceptionWhenEndDateBeforeStartDate() {
            projectRequest.setExpectedEndDate(LocalDate.now().minusDays(1));

            assertThatThrownBy(() -> projectService.create(projectRequest))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Expected end date must be after start date");
        }
    }

    @Nested
    @DisplayName("Update Project Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should update project successfully")
        void shouldUpdateProjectSuccessfully() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(memberService.findById(1L)).thenReturn(manager);
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse result = projectService.update(1L, projectRequest);

            assertThat(result).isNotNull();
            verify(projectMapper).updateEntity(projectRequest, project);
        }
    }

    @Nested
    @DisplayName("Delete Project Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete project with deletable status")
        void shouldDeleteProjectWithDeletableStatus() {
            project.setStatus(ProjectStatus.IN_ANALYSIS);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            projectService.delete(1L);

            verify(projectRepository).delete(project);
        }

        @Test
        @DisplayName("Should throw exception when deleting STARTED project")
        void shouldThrowExceptionWhenDeletingStartedProject() {
            project.setStatus(ProjectStatus.STARTED);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            assertThatThrownBy(() -> projectService.delete(1L))
                    .isInstanceOf(ProjectDeletionException.class)
                    .hasMessageContaining("Cannot delete project with status: STARTED");
        }

        @Test
        @DisplayName("Should throw exception when deleting IN_PROGRESS project")
        void shouldThrowExceptionWhenDeletingInProgressProject() {
            project.setStatus(ProjectStatus.IN_PROGRESS);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            assertThatThrownBy(() -> projectService.delete(1L))
                    .isInstanceOf(ProjectDeletionException.class)
                    .hasMessageContaining("Cannot delete project with status: IN_PROGRESS");
        }

        @Test
        @DisplayName("Should throw exception when deleting CLOSED project")
        void shouldThrowExceptionWhenDeletingClosedProject() {
            project.setStatus(ProjectStatus.CLOSED);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            assertThatThrownBy(() -> projectService.delete(1L))
                    .isInstanceOf(ProjectDeletionException.class)
                    .hasMessageContaining("Cannot delete project with status: CLOSED");
        }

        @Test
        @DisplayName("Should allow deleting CANCELLED project")
        void shouldAllowDeletingCancelledProject() {
            project.setStatus(ProjectStatus.CANCELLED);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            projectService.delete(1L);

            verify(projectRepository).delete(project);
        }
    }

    @Nested
    @DisplayName("Update Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update status following valid sequence")
        void shouldUpdateStatusFollowingValidSequence() {
            project.setStatus(ProjectStatus.IN_ANALYSIS);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            projectService.updateStatus(1L, ProjectStatus.ANALYSIS_DONE);

            assertThat(project.getStatus()).isEqualTo(ProjectStatus.ANALYSIS_DONE);
        }

        @Test
        @DisplayName("Should throw exception for invalid status transition")
        void shouldThrowExceptionForInvalidStatusTransition() {
            project.setStatus(ProjectStatus.IN_ANALYSIS);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

            assertThatThrownBy(() -> projectService.updateStatus(1L, ProjectStatus.STARTED))
                    .isInstanceOf(InvalidStatusTransitionException.class)
                    .hasMessageContaining("Invalid status transition from IN_ANALYSIS to STARTED");
        }

        @Test
        @DisplayName("Should allow transition to CANCELLED from any status")
        void shouldAllowTransitionToCancelledFromAnyStatus() {
            project.setStatus(ProjectStatus.STARTED);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            projectService.updateStatus(1L, ProjectStatus.CANCELLED);

            assertThat(project.getStatus()).isEqualTo(ProjectStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should set actual end date when closing project")
        void shouldSetActualEndDateWhenClosingProject() {
            project.setStatus(ProjectStatus.IN_PROGRESS);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            projectService.updateStatus(1L, ProjectStatus.CLOSED);

            assertThat(project.getActualEndDate()).isNotNull();
            assertThat(project.getActualEndDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("Should complete full status sequence")
        void shouldCompleteFullStatusSequence() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(any(Project.class))).thenReturn(projectResponse);

            projectService.updateStatus(1L, ProjectStatus.ANALYSIS_DONE);
            assertThat(project.getStatus()).isEqualTo(ProjectStatus.ANALYSIS_DONE);

            projectService.updateStatus(1L, ProjectStatus.ANALYSIS_APPROVED);
            assertThat(project.getStatus()).isEqualTo(ProjectStatus.ANALYSIS_APPROVED);

            projectService.updateStatus(1L, ProjectStatus.STARTED);
            assertThat(project.getStatus()).isEqualTo(ProjectStatus.STARTED);

            projectService.updateStatus(1L, ProjectStatus.PLANNED);
            assertThat(project.getStatus()).isEqualTo(ProjectStatus.PLANNED);

            projectService.updateStatus(1L, ProjectStatus.IN_PROGRESS);
            assertThat(project.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);

            projectService.updateStatus(1L, ProjectStatus.CLOSED);
            assertThat(project.getStatus()).isEqualTo(ProjectStatus.CLOSED);
        }
    }

    @Nested
    @DisplayName("Member Allocation Tests")
    class MemberAllocationTests {

        @Test
        @DisplayName("Should allocate employee member to project")
        void shouldAllocateEmployeeMemberToProject() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(memberService.findById(2L)).thenReturn(employee);
            when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(false);
            when(projectMemberRepository.countActiveProjectsByMember(2L)).thenReturn(0);
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse result = projectService.allocateMember(1L, 2L);

            assertThat(result).isNotNull();
            assertThat(project.getMembers()).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception when allocating non-employee member")
        void shouldThrowExceptionWhenAllocatingNonEmployeeMember() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(memberService.findById(1L)).thenReturn(manager);

            assertThatThrownBy(() -> projectService.allocateMember(1L, 1L))
                    .isInstanceOf(MemberAllocationException.class)
                    .hasMessageContaining("Only members with role EMPLOYEE can be allocated");
        }

        @Test
        @DisplayName("Should throw exception when project has maximum members")
        void shouldThrowExceptionWhenProjectHasMaximumMembers() {
            for (int i = 0; i < 10; i++) {
                project.getMembers().add(ProjectMember.builder()
                        .member(Member.builder().id((long) i + 10).build())
                        .build());
            }

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(memberService.findById(2L)).thenReturn(employee);

            assertThatThrownBy(() -> projectService.allocateMember(1L, 2L))
                    .isInstanceOf(MemberAllocationException.class)
                    .hasMessageContaining("maximum number of members (10)");
        }

        @Test
        @DisplayName("Should throw exception when member already allocated")
        void shouldThrowExceptionWhenMemberAlreadyAllocated() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(memberService.findById(2L)).thenReturn(employee);
            when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(true);

            assertThatThrownBy(() -> projectService.allocateMember(1L, 2L))
                    .isInstanceOf(MemberAllocationException.class)
                    .hasMessageContaining("Member is already allocated to this project");
        }

        @Test
        @DisplayName("Should throw exception when member has 3 active projects")
        void shouldThrowExceptionWhenMemberHas3ActiveProjects() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(memberService.findById(2L)).thenReturn(employee);
            when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(false);
            when(projectMemberRepository.countActiveProjectsByMember(2L)).thenReturn(3);

            assertThatThrownBy(() -> projectService.allocateMember(1L, 2L))
                    .isInstanceOf(MemberAllocationException.class)
                    .hasMessageContaining("maximum number of active projects (3)");
        }

        @Test
        @DisplayName("Should allow allocation when member has 2 active projects")
        void shouldAllowAllocationWhenMemberHas2ActiveProjects() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(memberService.findById(2L)).thenReturn(employee);
            when(projectMemberRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(false);
            when(projectMemberRepository.countActiveProjectsByMember(2L)).thenReturn(2);
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse result = projectService.allocateMember(1L, 2L);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Member Deallocation Tests")
    class MemberDeallocationTests {

        @Test
        @DisplayName("Should deallocate member from project")
        void shouldDeallocateMemberFromProject() {
            ProjectMember projectMember = ProjectMember.builder()
                    .id(1L)
                    .project(project)
                    .member(employee)
                    .build();
            project.getMembers().add(projectMember);
            project.getMembers().add(ProjectMember.builder()
                    .id(2L)
                    .project(project)
                    .member(Member.builder().id(3L).build())
                    .build());

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.findByProjectIdAndMemberId(1L, 2L)).thenReturn(Optional.of(projectMember));
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse result = projectService.deallocateMember(1L, 2L);

            assertThat(result).isNotNull();
            verify(projectMemberRepository).delete(projectMember);
        }

        @Test
        @DisplayName("Should throw exception when deallocation leaves project without members")
        void shouldThrowExceptionWhenDeallocationLeavesProjectWithoutMembers() {
            ProjectMember projectMember = ProjectMember.builder()
                    .id(1L)
                    .project(project)
                    .member(employee)
                    .build();
            project.getMembers().add(projectMember);
            project.setStatus(ProjectStatus.IN_PROGRESS);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.findByProjectIdAndMemberId(1L, 2L)).thenReturn(Optional.of(projectMember));

            assertThatThrownBy(() -> projectService.deallocateMember(1L, 2L))
                    .isInstanceOf(MemberAllocationException.class)
                    .hasMessageContaining("must have at least one member");
        }

        @Test
        @DisplayName("Should allow deallocation leaving no members when project is CLOSED")
        void shouldAllowDeallocationWhenProjectIsClosed() {
            ProjectMember projectMember = ProjectMember.builder()
                    .id(1L)
                    .project(project)
                    .member(employee)
                    .build();
            project.getMembers().add(projectMember);
            project.setStatus(ProjectStatus.CLOSED);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.findByProjectIdAndMemberId(1L, 2L)).thenReturn(Optional.of(projectMember));
            when(projectMapper.toResponse(project)).thenReturn(projectResponse);

            ProjectResponse result = projectService.deallocateMember(1L, 2L);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when member not found in project")
        void shouldThrowExceptionWhenMemberNotFoundInProject() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectMemberRepository.findByProjectIdAndMemberId(1L, 99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> projectService.deallocateMember(1L, 99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Member allocation not found");
        }
    }
}
