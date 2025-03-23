package dev.muazmemis.finalproject.controller;

import java.util.List;

import dev.muazmemis.finalproject.dto.project.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import dev.muazmemis.finalproject.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "Project management APIs")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Create a new project", description = "Create a new project with the provided details")
    public ResponseEntity<ProjectResponse> saveProject(@RequestBody @Valid ProjectRequest request) {
        return ResponseEntity.ok(projectService.saveProject(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Get a project by ID", description = "Get a project's details by its ID")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Get all projects", description = "Get a list of all projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Get projects by department", description = "Get a list of projects for a specific department")
    public ResponseEntity<List<ProjectResponse>> getProjectsByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(projectService.getProjectsByDepartment(departmentId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Update a project", description = "Update a project's details by its ID")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @RequestBody @Valid ProjectUpdateRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Update project status", description = "Update a project's status by its ID")
    public ResponseEntity<ProjectResponse> updateProjectStatus(
            @PathVariable Long id,
            @RequestBody @Valid ProjectStatusUpdateRequest request) {
        return ResponseEntity.ok(projectService.updateProductStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Delete a project", description = "Delete a project by its ID")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/team-members")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER')")
    @Operation(summary = "Add team members to a project", description = "Add multiple team members to a project")
    public ResponseEntity<ProjectResponse> addTeamMembers(
            @PathVariable Long id,
            @RequestBody @Valid TeamMembersRequest request) {
        return ResponseEntity.ok(projectService.addTeamMembers(id, request.userIds()));
    }

    @DeleteMapping("/{id}/team-members")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER')")
    @Operation(summary = "Remove team members from a project", description = "Remove multiple team members from a project")
    public ResponseEntity<ProjectResponse> removeTeamMembers(
            @PathVariable Long id,
            @RequestBody @Valid TeamMembersRequest request) {
        return ResponseEntity.ok(projectService.removeTeamMembers(id, request.userIds()));
    }

}
