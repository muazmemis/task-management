package dev.muazmemis.finalproject.service;

import java.util.ArrayList;
import java.util.List;

import dev.muazmemis.finalproject.constant.ErrorMessages;
import dev.muazmemis.finalproject.dto.project.ProjectStatusUpdateRequest;
import dev.muazmemis.finalproject.model.entity.Task;
import dev.muazmemis.finalproject.model.enums.TaskState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.muazmemis.finalproject.dto.project.ProjectRequest;
import dev.muazmemis.finalproject.dto.project.ProjectResponse;
import dev.muazmemis.finalproject.dto.project.ProjectUpdateRequest;
import dev.muazmemis.finalproject.mapper.ProjectMapper;
import dev.muazmemis.finalproject.model.entity.Department;
import dev.muazmemis.finalproject.model.entity.Project;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.model.enums.ProjectStatus;
import dev.muazmemis.finalproject.repository.DepartmentRepository;
import dev.muazmemis.finalproject.repository.ProjectRepository;
import dev.muazmemis.finalproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectResponse saveProject(ProjectRequest request) {
        Department department = departmentRepository.findByIdAndActiveTrue(request.departmentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.DEPARTMENT_NOT_FOUND, request.departmentId())
                ));

        Project project = projectMapper.toEntity(request);
        project.setDepartment(department);

        if (request.teamMemberIds() != null && !request.teamMemberIds().isEmpty()) {
            List<User> teamMembers = new ArrayList<>();
            for (Long userId : request.teamMemberIds()) {
                User user = userRepository.findByIdAndActiveTrue(userId)
                        .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, userId)));
                teamMembers.add(user);
            }
            project.setTeamMembers(teamMembers);
        }

        project = projectRepository.save(project);

        log.info("Project saved: {}", project.getTitle());
        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findByIdAndActiveTrue(id).orElseThrow(() ->
                new EntityNotFoundException(String.format(ErrorMessages.PROJECT_NOT_FOUND, id)));

        log.info("Project found: {}", project.getTitle());
        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAllByActiveTrue();

        log.info("Found {} projects", projects.size());
        return projectMapper.toResponseList(projects);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByDepartment(Long departmentId) {
        List<Project> projects = projectRepository.findByDepartmentIdAndActiveTrue(departmentId);

        log.info("Found {} projects getDepartmantId", projects.size());
        return projectMapper.toResponseList(projects);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectUpdateRequest request) {
        Project project = projectRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.PROJECT_NOT_FOUND, id)));

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update a completed project");
        }

        projectMapper.updateEntity(project, request);

        if (request.teamMemberIds() != null) {
            List<User> teamMembers = new ArrayList<>();
            for (Long userId : request.teamMemberIds()) {
                User user = userRepository.findByIdAndActiveTrue(userId).orElseThrow(() ->
                        new EntityNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, userId)));
                teamMembers.add(user);
            }
            project.setTeamMembers(teamMembers);
        }

        log.info("Project updated: {}", project.getTitle());
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse updateProductStatus(Long id, ProjectStatusUpdateRequest request) {
        Project project = projectRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.PROJECT_NOT_FOUND, id)));

        validateProjectStatusUpdate(project, request);
        project.setStatus(request.status());

        log.info("Project status updated. Project Title: '{}', New Status: '{}'", project.getTitle(), project.getStatus());
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        project.setActive(false);

        log.info("Project deleted: {}", project.getTitle());
        projectRepository.save(project);
    }

    @Transactional
    public ProjectResponse addTeamMembers(Long projectId, List<Long> userIds) {
        Project project = projectRepository.findByIdAndActiveTrue(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.PROJECT_NOT_FOUND, projectId)));

        for (Long userId : userIds) {
            User user = userRepository.findByIdAndActiveTrue(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, userId)));

            if (!project.getTeamMembers().contains(user)) {
                project.getTeamMembers().add(user);
            }
        }

        project = projectRepository.save(project);

        log.info("Project added team members: {}", project.getTitle());
        return projectMapper.toResponse(project);
    }

    @Transactional
    public ProjectResponse removeTeamMembers(Long projectId, List<Long> userIds) {
        Project project = projectRepository.findByIdAndActiveTrue(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.PROJECT_NOT_FOUND, projectId)));

        List<User> usersToRemove = new ArrayList<>();
        for (Long userId : userIds) {
            User user = userRepository.findByIdAndActiveTrue(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, userId)));

            if (project.getTeamMembers().contains(user)) {
                usersToRemove.add(user);
            }
        }

        project.getTeamMembers().removeAll(usersToRemove);
        project = projectRepository.save(project);

        log.info("Project removed team members: {}", project.getTitle());
        return projectMapper.toResponse(project);
    }

    private void validateProjectStatusUpdate(Project project, ProjectStatusUpdateRequest request) {
        if (project.getStatus() == request.status())
            throw new IllegalStateException("Project is already in the requested state");

        if (project.getStatus() == ProjectStatus.COMPLETED)
            throw new IllegalStateException("Cannot update a completed project");

        if (project.getStatus() == ProjectStatus.CANCELLED)
            throw new IllegalStateException("Cannot update a cancelled project");

        if (request.status() == ProjectStatus.COMPLETED && project.getTasks() != null) {
            for (Task task : project.getTasks()) {
                if (task.getState() != TaskState.COMPLETED) {
                    throw new IllegalStateException("Cannot update project status while there are incomplete tasks");
                }
            }
        }
    }

}
