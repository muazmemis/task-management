package dev.muazmemis.finalproject.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import dev.muazmemis.finalproject.dto.task.TaskRequest;
import dev.muazmemis.finalproject.dto.task.TaskResponse;
import dev.muazmemis.finalproject.dto.task.TaskStateUpdateRequest;
import dev.muazmemis.finalproject.dto.task.TaskUpdateRequest;
import dev.muazmemis.finalproject.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER')")
    @Operation(summary = "Create a new task", description = "Create a new task with the provided details")
    public ResponseEntity<TaskResponse> saveTask(@ModelAttribute @Valid TaskRequest request) throws IOException {
        return ResponseEntity.ok(taskService.saveTask(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Get a task by ID", description = "Get a task's details by its ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Get all tasks", description = "Get a list of all tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER')")
    @Operation(summary = "Update a task", description = "Update a task's details by its ID")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @PatchMapping("/{id}/state")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Update task state", description = "Update a task's state by its ID")
    public ResponseEntity<TaskResponse> updateTaskState(
            @PathVariable Long id,
            @RequestBody @Valid TaskStateUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateTaskState(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER')")
    @Operation(summary = "Delete a task", description = "Delete a task by its ID")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
