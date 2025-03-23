package dev.muazmemis.finalproject.controller;

import static dev.muazmemis.finalproject.constant.EndPoints.DEPARTMENT_ENDPOINT;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import dev.muazmemis.finalproject.dto.department.DepartmentRequest;
import dev.muazmemis.finalproject.dto.department.DepartmentResponse;
import dev.muazmemis.finalproject.service.DepartmentService;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();
    }

    @Test
    void saveDepartment_Success() throws Exception {
        DepartmentRequest request = new DepartmentRequest("IT");
        DepartmentResponse response = new DepartmentResponse(1L, "IT", List.of());

        when(departmentService.saveDepartment(any(DepartmentRequest.class))).thenReturn(response);

        mockMvc.perform(post(DEPARTMENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("IT")));
    }

    @Test
    void getDepartmentById_Success() throws Exception {
        DepartmentResponse response = new DepartmentResponse(1L, "IT", List.of());

        when(departmentService.getDepartmentById(1L)).thenReturn(response);

        mockMvc.perform(get(DEPARTMENT_ENDPOINT + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("IT")));
    }

    @Test
    void getAllDepartments_Success() throws Exception {
        DepartmentResponse response = new DepartmentResponse(1L, "IT", List.of());

        when(departmentService.getAllDepartments()).thenReturn(List.of(response));

        mockMvc.perform(get(DEPARTMENT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("IT")));
    }

    @Test
    void updateDepartment_Success() throws Exception {
        DepartmentRequest request = new DepartmentRequest("IT Updated");
        DepartmentResponse response = new DepartmentResponse(1L, "IT Updated", List.of());

        when(departmentService.updateDepartment(eq(1L), any(DepartmentRequest.class))).thenReturn(response);

        mockMvc.perform(put(DEPARTMENT_ENDPOINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("IT Updated")));
    }

    @Test
    void deleteDepartment_Success() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();

        mockMvc.perform(delete(DEPARTMENT_ENDPOINT + "/1"))
                .andExpect(status().isNoContent());

        verify(departmentService).deleteDepartment(1L);
    }
}
