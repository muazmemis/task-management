package dev.muazmemis.finalproject.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.muazmemis.finalproject.dto.task.TaskRequest;
import dev.muazmemis.finalproject.dto.task.TaskResponse;
import dev.muazmemis.finalproject.dto.task.TaskUpdateRequest;
import dev.muazmemis.finalproject.model.entity.Task;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "state", constant = "BACKLOG")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "comments", expression = "java(new ArrayList<>())")
    @Mapping(target = "attachments", expression = "java(new ArrayList<>())")
    Task toEntity(TaskRequest request);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "commentIds", expression = "java(task.getComments().stream().filter(comment -> comment.isActive()).map(comment -> comment.getId()).toList())")
    @Mapping(target = "attachmentIds", expression = "java(task.getAttachments().stream().filter(attachment -> attachment.isActive()).map(attachment -> attachment.getId()).toList())")
    TaskResponse toResponse(Task task);

    List<TaskResponse> toResponseList(List<Task> tasks);

    void updateEntity(@MappingTarget Task task, TaskUpdateRequest request);

}
