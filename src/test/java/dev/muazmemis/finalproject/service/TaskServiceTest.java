package dev.muazmemis.finalproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.muazmemis.finalproject.dto.task.TaskRequest;
import dev.muazmemis.finalproject.dto.task.TaskResponse;
import dev.muazmemis.finalproject.dto.task.TaskStateUpdateRequest;
import dev.muazmemis.finalproject.dto.task.TaskUpdateRequest;
import dev.muazmemis.finalproject.exception.TaskStateException;
import dev.muazmemis.finalproject.mapper.TaskMapper;
import dev.muazmemis.finalproject.model.entity.Project;
import dev.muazmemis.finalproject.model.entity.Task;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.model.enums.ProjectStatus;
import dev.muazmemis.finalproject.model.enums.Role;
import dev.muazmemis.finalproject.model.enums.TaskPriority;
import dev.muazmemis.finalproject.model.enums.TaskState;
import dev.muazmemis.finalproject.repository.ProjectRepository;
import dev.muazmemis.finalproject.repository.TaskRepository;
import dev.muazmemis.finalproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Project project;
    private Task task;
    private User user;
    private TaskRequest request;
    private TaskResponse response;
    private TaskUpdateRequest updateRequest;
    private TaskStateUpdateRequest stateUpdateRequest;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .title("Project 1")
                .status(ProjectStatus.IN_PROGRESS)
                .active(true)
                .build();

        user = User.builder()
                .id(1L)
                .username("user@example.com")
                .role(Role.PROJECT_MANAGER)
                .active(true)
                .build();

        task = Task.builder()
                .id(1L)
                .title("Task 1")
                .userStory("User Story")
                .acceptanceCriteria("Acceptance Criteria")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.HIGH)
                .project(project)
                .assignee(user)
                .active(true)
                .build();

        request = new TaskRequest("Task 1", "User Story", "Acceptance Criteria", TaskPriority.HIGH, 1L, 1L, null);

        response = new TaskResponse(1L, "Task 1", "User Story", "Acceptance Criteria", TaskState.BACKLOG,
                TaskPriority.HIGH, null, 1L, 1L, null, null);

        updateRequest = new TaskUpdateRequest("Updated Task", "Updated User Story", "Updated Acceptance Criteria",
                TaskState.BACKLOG, TaskPriority.MEDIUM, null, 1L);

        stateUpdateRequest = new TaskStateUpdateRequest(TaskState.IN_ANALYSIS, "Reason");

    }

    @Test
    void saveTask_Success() throws IOException {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(taskMapper.toEntity(request)).thenReturn(task);

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.saveTask(request);

        assertNotNull(result);
        assertEquals(request.title(), result.title());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void saveTask_ProjectNotFound_ThrowsException() {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.saveTask(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    void saveTask_CancelledProject_ThrowsException() {
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> taskService.saveTask(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot create task in a cancelled project");
    }

    @Test
    void saveTask_CompletedProject_ThrowsException() {
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> taskService.saveTask(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot create task in a completed project");
    }

    @Test
    void getTaskById_Success() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(request.title(), result.title());
    }

    @Test
    void getTaskById_NotFound_ThrowsException() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getAllTasks_Success() {
        List<Task> tasks = List.of(task);
        List<TaskResponse> responses = List.of(response);

        when(taskRepository.findAllActiveTasks()).thenReturn(tasks);
        when(taskMapper.toResponseList(tasks)).thenReturn(responses);

        List<TaskResponse> result = taskService.getAllTasks();

        assertNotNull(result);
        assertThat(result).hasSize(1);
    }

    @Test
    void updateTask_Success() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.updateTask(1L, updateRequest);

        assertNotNull(result);
        verify(taskMapper).updateEntity(task, updateRequest);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_CompletedTask_ThrowsException() {
        task.setState(TaskState.COMPLETED);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.updateTask(1L, updateRequest))
                .isInstanceOf(TaskStateException.class)
                .hasMessageContaining("Cannot update a completed task");
    }

    @Test
    void updateTask_TaskNotFound_ThrowsException() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(1L, updateRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void updateTask_AssigneeNotFound_ThrowsException() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(1L, updateRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void updateTaskState_Success() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.updateTaskState(1L, stateUpdateRequest);

        assertNotNull(result);
        assertEquals(stateUpdateRequest.state(), task.getState());
        assertEquals(stateUpdateRequest.reason(), task.getStateChangeReason());
        verify(taskRepository).save(task);
    }

    @Test
    void updateTaskState_CompletedTask_ThrowsException() {
        task.setState(TaskState.COMPLETED);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.updateTaskState(1L, stateUpdateRequest))
                .isInstanceOf(TaskStateException.class)
                .hasMessageContaining("Cannot change state of a completed task");
    }

    @Test
    void updateTaskState_TaskNotFound_ThrowsException() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTaskState(1L, stateUpdateRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.deleteTask(1L);

        assertFalse(task.isActive());
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask_TaskNotFound_ThrowsException() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deleteTask_CompletedTask_ThrowsException() {
        task.setState(TaskState.COMPLETED);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.deleteTask(1L))
                .isInstanceOf(TaskStateException.class)
                .hasMessageContaining("Cannot delete a completed task");
    }

    @Test
    void updateTaskState_MissingReasonForCancelled_ThrowsException() {
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        TaskStateUpdateRequest invalidRequest = new TaskStateUpdateRequest(TaskState.CANCELLED, null);

        assertThatThrownBy(() -> taskService.updateTaskState(1L, invalidRequest))
                .isInstanceOf(TaskStateException.class)
                .hasMessageContaining("Reason is required when changing state to CANCELLED");
    }

    @Test
    void updateTaskState_MissingReasonForBlocked_ThrowsException() {
        task.setState(TaskState.IN_PROGRESS);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        TaskStateUpdateRequest invalidRequest = new TaskStateUpdateRequest(TaskState.BLOCKED, null);

        assertThatThrownBy(() -> taskService.updateTaskState(1L, invalidRequest))
                .isInstanceOf(TaskStateException.class)
                .hasMessageContaining("Reason is required when changing state to CANCELLED or BLOCKED");
    }

    @Test
    void updateTaskState_BacklogToInProgress_ThrowsException() {
        task.setState(TaskState.BACKLOG);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        TaskStateUpdateRequest invalidRequest = new TaskStateUpdateRequest(TaskState.IN_PROGRESS, "Invalid transition");

        assertThatThrownBy(() -> taskService.updateTaskState(1L, invalidRequest))
                .isInstanceOf(TaskStateException.class)
                .hasMessageContaining("Tasks in BACKLOG can only move to IN_ANALYSIS");
    }

    @Test
    void updateTaskState_InAnalysisToCompleted_ThrowsException() {
        task.setState(TaskState.IN_ANALYSIS);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        TaskStateUpdateRequest invalidRequest = new TaskStateUpdateRequest(TaskState.COMPLETED, "Invalid transition");

        assertThatThrownBy(() -> taskService.updateTaskState(1L, invalidRequest))
                .isInstanceOf(TaskStateException.class)
                .hasMessageContaining("Tasks in IN_ANALYSIS can only move to BACKLOG or IN_PROGRESS");
    }

    @Test
    void updateTaskState_BacklogToCancelled_Success() {
        task.setState(TaskState.BACKLOG);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskStateUpdateRequest cancelRequest = new TaskStateUpdateRequest(TaskState.CANCELLED, "Cancellation reason");

        TaskResponse result = taskService.updateTaskState(1L, cancelRequest);

        assertNotNull(result);
        assertEquals(TaskState.CANCELLED, task.getState());
    }

    @Test
    void updateTaskState_BlockedForInvalidState_ThrowsException() {
        task.setState(TaskState.BACKLOG);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        TaskStateUpdateRequest invalidRequest = new TaskStateUpdateRequest(TaskState.BLOCKED, "Blocking reason");

        assertThatThrownBy(() -> taskService.updateTaskState(1L, invalidRequest))
                .isInstanceOf(TaskStateException.class)
                .hasMessageContaining("Only tasks in IN_ANALYSIS or IN_PROGRESS can be blocked");
    }

    @Test
    void updateTaskState_BlockedForValidState_Success() {
        task.setState(TaskState.IN_PROGRESS);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskStateUpdateRequest blockRequest = new TaskStateUpdateRequest(TaskState.BLOCKED, "Blocking reason");

        TaskResponse result = taskService.updateTaskState(1L, blockRequest);

        assertNotNull(result);
        assertEquals(TaskState.BLOCKED, task.getState());
    }

    @Test
    void updateTaskState_InProgressToCompleted_Success() {
        task.setState(TaskState.IN_PROGRESS);
        when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskStateUpdateRequest completeRequest = new TaskStateUpdateRequest(TaskState.COMPLETED, "Completion reason");

        TaskResponse result = taskService.updateTaskState(1L, completeRequest);

        assertNotNull(result);
        assertEquals(TaskState.COMPLETED, task.getState());
    }

    @Test
    void saveTask_WithAssignee_Success() throws IOException {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toEntity(any(TaskRequest.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskRequest requestWithAssignee = new TaskRequest(
                "Test Task",
                "Test User Story",
                "Test Acceptance Criteria",
                task.getPriority(),
                1L,
                1L,
                null);

        TaskResponse result = taskService.saveTask(requestWithAssignee);

        assertNotNull(result);
        verify(taskRepository).save(task);
        verify(userRepository).findByIdAndActiveTrue(1L);
    }

    @Test
    void saveTask_WithInvalidAssignee_ThrowsException() {
        when(projectRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByIdAndActiveTrue(999L)).thenReturn(Optional.empty());
        when(taskMapper.toEntity(any(TaskRequest.class))).thenReturn(task);

        TaskRequest requestWithInvalidAssignee = new TaskRequest(
                "Test Task",
                "Test User Story",
                "Test Acceptance Criteria",
                task.getPriority(),
                1L,
                999L,
                null);

        assertThatThrownBy(() -> taskService.saveTask(requestWithInvalidAssignee))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
