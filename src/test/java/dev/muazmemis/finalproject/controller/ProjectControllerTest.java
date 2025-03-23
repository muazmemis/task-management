package dev.muazmemis.finalproject.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.muazmemis.finalproject.dto.project.ProjectRequest;
import dev.muazmemis.finalproject.dto.project.ProjectResponse;
import dev.muazmemis.finalproject.dto.project.ProjectStatusUpdateRequest;
import dev.muazmemis.finalproject.dto.project.ProjectUpdateRequest;
import dev.muazmemis.finalproject.dto.project.TeamMembersRequest;
import dev.muazmemis.finalproject.model.enums.ProjectStatus;
import dev.muazmemis.finalproject.service.ProjectService;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProjectRequest request;
    private ProjectResponse response;
    private ProjectUpdateRequest updateRequest;
    private ProjectStatusUpdateRequest statusUpdateRequest;
    private TeamMembersRequest teamMembersRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .build();

        request = new ProjectRequest("Project 1", "Description", 1L, List.of(1L, 2L));

        response = new ProjectResponse(1L, "Project 1", "Description", ProjectStatus.IN_PROGRESS, "Department 1",
                List.of(1L, 2L), List.of(1L, 2L));

        updateRequest = new ProjectUpdateRequest("Updated Project", "Updated Description", ProjectStatus.IN_PROGRESS,
                List.of(1L, 2L));

        statusUpdateRequest = new ProjectStatusUpdateRequest(ProjectStatus.COMPLETED);

        teamMembersRequest = new TeamMembersRequest(List.of(1L, 2L));
    }

    @Test
    void createProject_Success() throws Exception {
        when(projectService.saveProject(any(ProjectRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Project 1"));
    }

    @Test
    void getProjectById_Success() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Project 1"));
    }

    @Test
    void getAllProjects_Success() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Project 1"));
    }

    @Test
    void getProjectsByDepartment_Success() throws Exception {
        when(projectService.getProjectsByDepartment(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/projects/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Project 1"));
    }

    @Test
    void updateProject_Success() throws Exception {
        when(projectService.updateProject(eq(1L), any(ProjectUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Project 1"));
    }

    @Test
    void updateProjectStatus_Success() throws Exception {
        when(projectService.updateProductStatus(eq(1L), any(ProjectStatusUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/projects/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Project 1"));
    }

    @Test
    void deleteProject_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject(1L);
    }

    @Test
    void addTeamMembers_Success() throws Exception {
        when(projectService.addTeamMembers(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/projects/1/team-members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamMembersRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Project 1"));
    }

    @Test
    void removeTeamMembers_Success() throws Exception {
        when(projectService.removeTeamMembers(eq(1L), any())).thenReturn(response);

        mockMvc.perform(delete("/api/v1/projects/1/team-members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teamMembersRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Project 1"));
    }
}
