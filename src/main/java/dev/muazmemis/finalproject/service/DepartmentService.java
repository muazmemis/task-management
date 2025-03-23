package dev.muazmemis.finalproject.service;

import java.util.List;

import dev.muazmemis.finalproject.constant.ErrorMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.muazmemis.finalproject.dto.department.DepartmentRequest;
import dev.muazmemis.finalproject.dto.department.DepartmentResponse;
import dev.muazmemis.finalproject.mapper.DepartmentMapper;
import dev.muazmemis.finalproject.model.entity.Department;
import dev.muazmemis.finalproject.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional
    public DepartmentResponse saveDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByNameAndActiveTrue(request.name())) {
            throw new IllegalStateException(String.format(ErrorMessages.DEPARTMENT_NAME_EXISTS, request.name()));
        }

        Department department = departmentMapper.toEntity(request);
        department = departmentRepository.save(department);

        log.info("Department saved: {}", department.getName());
        return departmentMapper.toResponse(department);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.DEPARTMENT_NOT_FOUND, id)));

        log.info("Department found: {}", department.getName());
        return departmentMapper.toResponse(department);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        List<Department> departments = departmentRepository.findAllByActiveTrue();
        log.info("Departments found: {}", departments.size());
        return departmentMapper.toResponseList(departments);
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.DEPARTMENT_NOT_FOUND, id)));

        if (!department.getName().equals(request.name())
                && departmentRepository.existsByNameAndActiveTrue(request.name())) {
            throw new IllegalStateException(String.format(ErrorMessages.DEPARTMENT_NAME_EXISTS, request.name()));
        }

        department.setName(request.name());
        department = departmentRepository.save(department);

        log.info("Department updated: {}", department.getName());
        return departmentMapper.toResponse(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.DEPARTMENT_NOT_FOUND, id)));
        department.setActive(false);

        log.info("Department deleted: {}", department.getName());
        departmentRepository.save(department);
    }
}
