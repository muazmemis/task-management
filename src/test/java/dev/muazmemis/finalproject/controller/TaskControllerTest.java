package dev.muazmemis.finalproject.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import dev.muazmemis.finalproject.constant.EndPoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.muazmemis.finalproject.dto.task.TaskRequest;
import dev.muazmemis.finalproject.dto.task.TaskResponse;
import dev.muazmemis.finalproject.dto.task.TaskStateUpdateRequest;
import dev.muazmemis.finalproject.dto.task.TaskUpdateRequest;
import dev.muazmemis.finalproject.model.enums.TaskPriority;
import dev.muazmemis.finalproject.model.enums.TaskState;
import dev.muazmemis.finalproject.service.TaskService;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private TaskResponse taskResponse;
    private TaskUpdateRequest taskUpdateRequest;
    private TaskStateUpdateRequest taskStateUpdateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        taskResponse = new TaskResponse(
                1L,
                "Task Title",
                "User Story",
                "Acceptance Criteria",
                TaskState.BACKLOG,
                TaskPriority.HIGH,
                null,
                1L,
                1L,
                new ArrayList<>(),
                new ArrayList<>());

        taskUpdateRequest = new TaskUpdateRequest(
                "Updated Task Title",
                "Updated User Story",
                "Updated Acceptance Criteria",
                TaskState.IN_PROGRESS,
                TaskPriority.MEDIUM,
                null,
                2L);

        taskStateUpdateRequest = new TaskStateUpdateRequest(
                TaskState.IN_ANALYSIS,
                "State change reason");
    }

    @Test
    void saveTask_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.txt",
                "text/plain",
                "test content".getBytes());

        when(taskService.saveTask(any(TaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(post(EndPoints.TASK_ENDPOINT)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .param("title", "Task Title")
                        .param("userStory", "User Story")
                        .param("acceptanceCriteria", "Acceptance Criteria")
                        .param("priority", "HIGH")
                        .param("projectId", "1")
                        .param("assigneeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Task Title"));
    }

    @Test
    void getTaskById_Success() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskResponse);

        mockMvc.perform(get(EndPoints.TASK_ENDPOINT + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Task Title"));
    }

    @Test
    void getAllTasks_Success() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(taskResponse));

        mockMvc.perform(get(EndPoints.TASK_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Task Title"));
    }

    @Test
    void updateTask_Success() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskUpdateRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(put(EndPoints.TASK_ENDPOINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Task Title"));
    }

    @Test
    void updateTaskState_Success() throws Exception {
        when(taskService.updateTaskState(eq(1L), any(TaskStateUpdateRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(patch(EndPoints.TASK_ENDPOINT + "/1/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStateUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Task Title"));
    }

    @Test
    void deleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete(EndPoints.TASK_ENDPOINT + "/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }
}
