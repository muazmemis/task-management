package dev.muazmemis.finalproject.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.muazmemis.finalproject.dto.comment.CommentRequest;
import dev.muazmemis.finalproject.dto.comment.CommentResponse;
import dev.muazmemis.finalproject.model.entity.Comment;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "active", constant = "true")
    Comment toEntity(CommentRequest request);

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "userId", source = "createdBy.id")
    @Mapping(target = "username", source = "createdBy.username")
    CommentResponse toResponse(Comment comment);

    List<CommentResponse> toResponseList(List<Comment> comments);
}
