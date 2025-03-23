package dev.muazmemis.finalproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.muazmemis.finalproject.constant.ErrorMessages;
import dev.muazmemis.finalproject.dto.department.DepartmentRequest;
import dev.muazmemis.finalproject.dto.department.DepartmentResponse;
import dev.muazmemis.finalproject.mapper.DepartmentMapper;
import dev.muazmemis.finalproject.model.entity.Department;
import dev.muazmemis.finalproject.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department;
    private DepartmentRequest request;
    private DepartmentResponse response;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("Department 1")
                .active(true)
                .build();

        request = new DepartmentRequest("IT");

        response = new DepartmentResponse(1L, "IT", List.of());
    }

    @Test
    void createDepartment_Success() {
        when(departmentRepository.existsByNameAndActiveTrue(request.name())).thenReturn(false);
        when(departmentMapper.toEntity(request)).thenReturn(department);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(departmentMapper.toResponse(department)).thenReturn(response);

        DepartmentResponse result = departmentService.saveDepartment(request);

        assertNotNull(result);
        assertEquals(request.name(), result.name());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void createDepartment_DuplicateName_ThrowsException() {
        when(departmentRepository.existsByNameAndActiveTrue(request.name())).thenReturn(true);

        assertThatThrownBy(() -> departmentService.saveDepartment(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format(ErrorMessages.DEPARTMENT_NAME_EXISTS, request.name()));
    }

    @Test
    void getDepartmentById_Success() {
        when(departmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(department));
        when(departmentMapper.toResponse(department)).thenReturn(response);

        DepartmentResponse result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals(request.name(), result.name());
    }

    @Test
    void getDepartmentById_NotFound_ThrowsException() {
        when(departmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format(ErrorMessages.DEPARTMENT_NOT_FOUND, 1L));
    }

    @Test
    void getAllDepartments_Success() {
        List<Department> departments = List.of(department);
        List<DepartmentResponse> responses = List.of(response);

        when(departmentRepository.findAllByActiveTrue()).thenReturn(departments);
        when(departmentMapper.toResponseList(departments)).thenReturn(responses);

        List<DepartmentResponse> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertThat(result).hasSize(1);
    }

    @Test
    void updateDepartment_Success() {
        when(departmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(departmentMapper.toResponse(department)).thenReturn(response);

        DepartmentResponse result = departmentService.updateDepartment(1L, request);

        assertNotNull(result);
        assertEquals(request.name(), result.name());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void updateDepartment_NewNameAlreadyExists_ThrowsException() {
        Department existingDepartment = Department.builder()
                .id(1L)
                .name("HR")
                .active(true)
                .build();
        DepartmentRequest updateRequest = new DepartmentRequest("IT");

        when(departmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.existsByNameAndActiveTrue(updateRequest.name())).thenReturn(true);

        assertThatThrownBy(() -> departmentService.updateDepartment(1L, updateRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format(ErrorMessages.DEPARTMENT_NAME_EXISTS, updateRequest.name()));
    }

    @Test
    void deleteDepartment_Success() {
        when(departmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(department));

        departmentService.deleteDepartment(1L);

        assertFalse(department.isActive());
        verify(departmentRepository).save(department);
    }

    @Test
    void deleteDepartment_NotFound_ThrowsException() {
        when(departmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.deleteDepartment(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format(ErrorMessages.DEPARTMENT_NOT_FOUND, 1L));
    }
}
