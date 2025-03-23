package dev.muazmemis.finalproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.muazmemis.finalproject.dto.project.ProjectRequest;
import dev.muazmemis.finalproject.dto.project.ProjectResponse;
import dev.muazmemis.finalproject.dto.project.ProjectStatusUpdateRequest;
import dev.muazmemis.finalproject.dto.project.ProjectUpdateRequest;
import dev.muazmemis.finalproject.mapper.ProjectMapper;
import dev.muazmemis.finalproject.model.entity.Department;
import dev.muazmemis.finalproject.model.entity.Project;
import dev.muazmemis.finalproject.model.entity.Task;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.model.enums.ProjectStatus;
import dev.muazmemis.finalproject.model.enums.Role;
import dev.muazmemis.finalproject.model.enums.TaskState;
import dev.muazmemis.finalproject.repository.DepartmentRepository;
import dev.muazmemis.finalproject.repository.ProjectRepository;
import dev.muazmemis.finalproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private Department department;
    private User user;
    private ProjectRequest request;
    private ProjectResponse response;
    private ProjectUpdateRequest updateRequest;
    private ProjectStatusUpdateRequest statusUpdateRequest;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("IT")
                .active(true)
                .build();

        user = User.builder()
                .id(1L)
                .username("user@example.com")
                .role(Role.PROJECT_MANAGER)
                .active(true)
                .build();

        project = Project.builder()
                .id(1L)
                .title("Project 1")
                .description("Description")
                .status(ProjectStatus.IN_PROGRESS)
                .department(department)
                .teamMembers(new ArrayList<>())
                .tasks(new ArrayList<>())
                .active(true)
                .build();

        request = new ProjectRequest("Project 1", "Description", 1L, List.of(1L));

        response = new ProjectResponse(1L, "Project 1", "Description", ProjectStatus.IN_PROGRESS, "IT", List.of(),
                List.of());

        updateRequest = new ProjectUpdateRequest("Updated Project", "Updated Description", ProjectStatus.IN_PROGRESS,
                List.of(1L));

        statusUpdateRequest = new ProjectStatusUpdateRequest(ProjectStatus.COMPLETED);
    }

    @Test
    void saveProject_Success() {
        when(departmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(department));
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));

        ProjectResponse result = projectService.saveProject(request);

        assertNotNull(result);
        assertEquals(request.title(), result.title());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void saveProject_DepartmentNotFound_ThrowsException() {
        when(departmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.saveProject(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Department not found");
    }

    @Test
    void getProjectById_Success() {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals(request.title(), result.title());
    }

    @Test
    void getProjectById_NotFound_ThrowsException() {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getAllProjects_Success() {
        List<Project> projects = List.of(project);
        List<ProjectResponse> responses = List.of(response);

        when(projectRepository.findAllByActiveTrue()).thenReturn(projects);
        when(projectMapper.toResponseList(projects)).thenReturn(responses);

        List<ProjectResponse> result = projectService.getAllProjects();

        assertNotNull(result);
        assertThat(result).hasSize(1);
    }

    @Test
    void getProjectsByDepartment_Success() {
        List<Project> projects = List.of(project);
        List<ProjectResponse> responses = List.of(response);

        when(projectRepository.findByDepartmentIdAndActiveTrue(1L)).thenReturn(projects);
        when(projectMapper.toResponseList(projects)).thenReturn(responses);

        List<ProjectResponse> result = projectService.getProjectsByDepartment(1L);

        assertNotNull(result);
        assertThat(result).hasSize(1);
    }

    @Test
    void updateProject_Success() {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));

        ProjectResponse result = projectService.updateProject(1L, updateRequest);

        assertNotNull(result);
        verify(projectMapper).updateEntity(project, updateRequest);
        verify(projectRepository).save(project);
    }

    @Test
    void updateProject_CompletedProject_ThrowsException() {
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.updateProject(1L, updateRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot update a completed project");
    }

    @Test
    void updateProjectStatus_Success() {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.updateProductStatus(1L, statusUpdateRequest);

        assertNotNull(result);
        assertEquals(statusUpdateRequest.status(), project.getStatus());
        verify(projectRepository).save(project);
    }

    @Test
    void updateProjectStatus_AlreadyCompleted_ThrowsException() {
        project.setStatus(ProjectStatus.COMPLETED);
        statusUpdateRequest = new ProjectStatusUpdateRequest(ProjectStatus.COMPLETED);
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.updateProductStatus(1L, statusUpdateRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Project is already in the requested state");
    }

    @Test
    void updateProjectStatus_IncompleteTasksForCompletion_ThrowsException() {
        Task incompleteTask = Task.builder()
                .id(1L)
                .state(TaskState.IN_PROGRESS)
                .build();
        project.getTasks().add(incompleteTask);

        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.updateProductStatus(1L, statusUpdateRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot update project status while there are incomplete tasks");
    }

    @Test
    void deleteProject_Success() {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        projectService.deleteProject(1L);

        assertFalse(project.isActive());
        verify(projectRepository).save(project);
    }

    @Test
    void addTeamMembers_Success() {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.addTeamMembers(1L, List.of(1L));

        assertNotNull(result);
        assertThat(project.getTeamMembers()).contains(user);
        verify(projectRepository).save(project);
    }

    @Test
    void removeTeamMembers_Success() {
        project.getTeamMembers().add(user);

        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse result = projectService.removeTeamMembers(1L, List.of(1L));

        assertNotNull(result);
        assertThat(project.getTeamMembers()).doesNotContain(user);
        verify(projectRepository).save(project);
    }
}
