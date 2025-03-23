package dev.muazmemis.finalproject.mapper;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import dev.muazmemis.finalproject.dto.project.ProjectRequest;
import dev.muazmemis.finalproject.dto.project.ProjectResponse;
import dev.muazmemis.finalproject.dto.project.ProjectUpdateRequest;
import dev.muazmemis.finalproject.model.entity.Project;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", imports = Collections.class)
public interface ProjectMapper {

    @Mapping(target = "status", constant = "IN_PROGRESS")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "tasks", expression = "java(new ArrayList<>())")
    @Mapping(target = "teamMembers", expression = "java(new ArrayList<>())")
    Project toEntity(ProjectRequest request);

    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "taskIds", expression = "java(project.getTasks() != null ? project.getTasks().stream().map(task -> task.getId()).toList() : Collections.emptyList())")
    @Mapping(target = "teamMemberIds", expression = "java(project.getTeamMembers() != null ? project.getTeamMembers().stream().map(user -> user.getId()).toList() : Collections.emptyList())")
    ProjectResponse toResponse(Project project);

    List<ProjectResponse> toResponseList(List<Project> projects);

    void updateEntity(@MappingTarget Project project, ProjectUpdateRequest request);
}
