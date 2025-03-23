package dev.muazmemis.finalproject.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.muazmemis.finalproject.constant.ErrorMessages;
import dev.muazmemis.finalproject.dto.task.TaskRequest;
import dev.muazmemis.finalproject.dto.task.TaskResponse;
import dev.muazmemis.finalproject.dto.task.TaskStateUpdateRequest;
import dev.muazmemis.finalproject.dto.task.TaskUpdateRequest;
import dev.muazmemis.finalproject.exception.TaskStateException;
import dev.muazmemis.finalproject.mapper.TaskMapper;
import dev.muazmemis.finalproject.model.entity.Attachment;
import dev.muazmemis.finalproject.model.entity.Project;
import dev.muazmemis.finalproject.model.entity.Task;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.model.enums.ProjectStatus;
import dev.muazmemis.finalproject.model.enums.TaskState;
import dev.muazmemis.finalproject.repository.AttachmentRepository;
import dev.muazmemis.finalproject.repository.ProjectRepository;
import dev.muazmemis.finalproject.repository.TaskRepository;
import dev.muazmemis.finalproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;

    @Transactional
    public TaskResponse saveTask(TaskRequest request) throws IOException {
        Project project = projectRepository.findByIdAndActiveTrue(request.projectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.PROJECT_NOT_FOUND, request.projectId())));

        if (project.getStatus() == ProjectStatus.COMPLETED)
            throw new IllegalStateException("Cannot create task in a completed project");

        if (project.getStatus() == ProjectStatus.CANCELLED)
            throw new IllegalStateException("Cannot create task in a cancelled project");

        Task task = taskMapper.toEntity(request);
        task.setProject(project);

        if (request.assigneeId() != null) {
            User assignee = userRepository.findByIdAndActiveTrue(request.assigneeId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(ErrorMessages.USERNAME_NOT_FOUND, request.assigneeId())));
            task.setAssignee(assignee);
        }

        task = taskRepository.save(task);

        if (request.files() != null && !request.files().isEmpty()) {
            List<Attachment> attachments = attachmentService.saveAttachmentFiles(request.files());
            Task finalTask = task;
            attachments.forEach(attachment -> attachment.setTask(finalTask));
            attachmentRepository.saveAll(attachments);
            task.setAttachments(attachments);
        }

        log.info("Task saved: {}", task.getTitle());
        return taskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.TASK_NOT_FOUND, id)));

        log.info("Task found: {}", task.getTitle());
        return taskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAllActiveTasks();

        log.info("Tasks found: {}", tasks.size());
        return taskMapper.toResponseList(tasks);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.TASK_NOT_FOUND, id)));

        if (task.getState() == TaskState.COMPLETED)
            throw new TaskStateException("Cannot update a completed task");

        taskMapper.updateEntity(task, request);

        if (request.assigneeId() != null) {
            User assignee = userRepository.findByIdAndActiveTrue(request.assigneeId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format(ErrorMessages.USERNAME_NOT_FOUND, request.assigneeId())));
            task.setAssignee(assignee);
        }

        log.info("Task updated: {}", task.getTitle());
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskState(Long id, TaskStateUpdateRequest request) {
        Task task = taskRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.TASK_NOT_FOUND, id)));

        validateTaskStateUpdate(task, request);

        task.setState(request.state());
        task.setStateChangeReason(request.reason());

        log.info("Task state updated: {} - {}", task.getTitle(), task.getState());
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.TASK_NOT_FOUND, id)));

        if (task.getState() == TaskState.COMPLETED)
            throw new TaskStateException("Cannot delete a completed task");

        task.setActive(false);

        log.info("Task deleted: {}", task.getTitle());
        taskRepository.save(task);
    }

    private void validateTaskStateUpdate(Task task, TaskStateUpdateRequest request) {
        if (task.getState() == TaskState.COMPLETED)
            throw new TaskStateException("Cannot change state of a completed task");

        if ((request.state() == TaskState.CANCELLED || request.state() == TaskState.BLOCKED)
                && request.reason() == null)
            throw new TaskStateException("Reason is required when changing state to CANCELLED or BLOCKED");

        validateStateTransition(task.getState(), request.state());
    }

    private void validateStateTransition(TaskState currentState, TaskState newState) {
        if (newState == TaskState.CANCELLED)
            return;

        if (newState == TaskState.BLOCKED) {
            if (currentState != TaskState.IN_ANALYSIS && currentState != TaskState.IN_PROGRESS)
                throw new TaskStateException("Only tasks in IN_ANALYSIS or IN_PROGRESS can be blocked");
            return;
        }

        switch (currentState) {
            case BACKLOG:
                if (newState != TaskState.IN_ANALYSIS)
                    throw new TaskStateException("Tasks in BACKLOG can only move to IN_ANALYSIS");
                return;
            case IN_ANALYSIS:
                if (newState != TaskState.BACKLOG && newState != TaskState.IN_PROGRESS)
                    throw new TaskStateException("Tasks in IN_ANALYSIS can only move to BACKLOG or IN_PROGRESS");
                return;
            case IN_PROGRESS:
                if (newState != TaskState.IN_ANALYSIS && newState != TaskState.COMPLETED)
                    throw new TaskStateException("Tasks in IN_PROGRESS can only move to IN_ANALYSIS or COMPLETED");
                return;
            default:
        }
    }
}
