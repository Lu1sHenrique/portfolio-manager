package com.portfolio.controller;

import com.portfolio.view.request.MemberAllocationRequest;
import com.portfolio.view.request.ProjectFilterRequest;
import com.portfolio.view.request.ProjectRequest;
import com.portfolio.view.request.ProjectStatusRequest;
import com.portfolio.view.response.ProjectResponse;
import com.portfolio.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "List all projects with pagination and filters")
    public ResponseEntity<Page<ProjectResponse>> findAll(
            @ModelAttribute ProjectFilterRequest filter,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(projectService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ProjectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update project status")
    public ResponseEntity<ProjectResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProjectStatusRequest request) {
        return ResponseEntity.ok(projectService.updateStatus(id, request.getNewStatus()));
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Allocate a member to a project")
    public ResponseEntity<ProjectResponse> allocateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberAllocationRequest request) {
        ProjectResponse response = projectService.allocateMember(id, request.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    @Operation(summary = "Deallocate a member from a project")
    public ResponseEntity<ProjectResponse> deallocateMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(projectService.deallocateMember(projectId, memberId));
    }
}
