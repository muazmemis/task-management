package dev.muazmemis.finalproject.mapper;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.muazmemis.finalproject.dto.department.DepartmentRequest;
import dev.muazmemis.finalproject.dto.department.DepartmentResponse;
import dev.muazmemis.finalproject.model.entity.Department;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", imports = Collections.class)
public interface DepartmentMapper {

    @Mapping(target = "active", constant = "true")
    Department toEntity(DepartmentRequest request);

    @Mapping(target = "projectIds", expression = "java(department.getProjects() != null ? department.getProjects().stream().map(project -> project.getId()).toList() : Collections.emptyList())")
    DepartmentResponse toResponse(Department department);

    List<DepartmentResponse> toResponseList(List<Department> departments);
}
