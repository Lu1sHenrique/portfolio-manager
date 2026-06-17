package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.view.request.MemberAllocationRequest;
import com.portfolio.view.request.ProjectFilterRequest;
import com.portfolio.view.request.ProjectRequest;
import com.portfolio.view.request.ProjectStatusRequest;
import com.portfolio.view.response.ProjectResponse;
import com.portfolio.model.enums.ProjectStatus;
import com.portfolio.exception.BusinessRuleException;
import com.portfolio.exception.InvalidStatusTransitionException;
import com.portfolio.exception.ProjectDeletionException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.security.JwtService;
import com.portfolio.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.portfolio.config.TestSecurityConfig;
import com.portfolio.config.TestJacksonConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import({TestSecurityConfig.class, TestJacksonConfig.class})
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private ProjectResponse projectResponse;
    private ProjectRequest projectRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        projectResponse = ProjectResponse.builder()
                .id(1L)
                .name("Test Project")
                .status(ProjectStatus.IN_ANALYSIS)
                .totalBudget(BigDecimal.valueOf(100000))
                .build();

        projectRequest = ProjectRequest.builder()
                .name("Test Project")
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(3))
                .totalBudget(BigDecimal.valueOf(100000))
                .managerId(1L)
                .build();

        userDetails = User.builder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();
    }

    @Test
    @DisplayName("GET /api/projects - Should return paginated projects")
    void shouldReturnPaginatedProjects() throws Exception {
        Page<ProjectResponse> page = new PageImpl<>(List.of(projectResponse));
        when(projectService.findAll(any(ProjectFilterRequest.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/projects")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Project"));
    }

    @Test
    @DisplayName("GET /api/projects/{id} - Should return project by id")
    void shouldReturnProjectById() throws Exception {
        when(projectService.findById(1L)).thenReturn(projectResponse);

        mockMvc.perform(get("/api/projects/1")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    @DisplayName("GET /api/projects/{id} - Should return 404 when not found")
    void shouldReturn404WhenProjectNotFound() throws Exception {
        when(projectService.findById(99L)).thenThrow(new ResourceNotFoundException("Project", 99L));

        mockMvc.perform(get("/api/projects/99")
                        .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/projects - Should create project")
    void shouldCreateProject() throws Exception {
        when(projectService.create(any(ProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(post("/api/projects")
                        .with(user(userDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/projects - Should return 400 for invalid request")
    void shouldReturn400ForInvalidRequest() throws Exception {
        ProjectRequest invalidRequest = ProjectRequest.builder().build();

        mockMvc.perform(post("/api/projects")
                        .with(user(userDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/projects/{id} - Should update project")
    void shouldUpdateProject() throws Exception {
        when(projectService.update(eq(1L), any(ProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(put("/api/projects/1")
                        .with(user(userDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} - Should delete project")
    void shouldDeleteProject() throws Exception {
        doNothing().when(projectService).delete(1L);

        mockMvc.perform(delete("/api/projects/1")
                        .with(user(userDetails))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} - Should return 422 when cannot delete")
    void shouldReturn422WhenCannotDelete() throws Exception {
        doThrow(new ProjectDeletionException(ProjectStatus.STARTED))
                .when(projectService).delete(1L);

        mockMvc.perform(delete("/api/projects/1")
                        .with(user(userDetails))
                        .with(csrf()))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH /api/projects/{id}/status - Should update status")
    void shouldUpdateStatus() throws Exception {
        ProjectStatusRequest statusRequest = ProjectStatusRequest.builder()
                .newStatus(ProjectStatus.ANALYSIS_DONE)
                .build();
        when(projectService.updateStatus(1L, ProjectStatus.ANALYSIS_DONE)).thenReturn(projectResponse);

        mockMvc.perform(patch("/api/projects/1/status")
                        .with(user(userDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/projects/{id}/status - Should return 422 for invalid transition")
    void shouldReturn422ForInvalidTransition() throws Exception {
        ProjectStatusRequest statusRequest = ProjectStatusRequest.builder()
                .newStatus(ProjectStatus.CLOSED)
                .build();
        when(projectService.updateStatus(1L, ProjectStatus.CLOSED))
                .thenThrow(new InvalidStatusTransitionException(ProjectStatus.IN_ANALYSIS, ProjectStatus.CLOSED));

        mockMvc.perform(patch("/api/projects/1/status")
                        .with(user(userDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /api/projects/{id}/members - Should allocate member")
    void shouldAllocateMember() throws Exception {
        MemberAllocationRequest request = MemberAllocationRequest.builder()
                .memberId(2L)
                .build();
        when(projectService.allocateMember(1L, 2L)).thenReturn(projectResponse);

        mockMvc.perform(post("/api/projects/1/members")
                        .with(user(userDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("DELETE /api/projects/{projectId}/members/{memberId} - Should deallocate member")
    void shouldDeallocateMember() throws Exception {
        when(projectService.deallocateMember(1L, 2L)).thenReturn(projectResponse);

        mockMvc.perform(delete("/api/projects/1/members/2")
                        .with(user(userDetails))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
