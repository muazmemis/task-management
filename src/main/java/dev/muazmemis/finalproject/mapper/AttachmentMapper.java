package dev.muazmemis.finalproject.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.muazmemis.finalproject.dto.attachment.AttachmentResponse;
import dev.muazmemis.finalproject.model.entity.Attachment;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AttachmentMapper {

    Attachment toEntity(AttachmentResponse response);
    List<Attachment> toEntityList(List<AttachmentResponse> responses);

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "userId", source = "createdBy.id")
    @Mapping(target = "username", source = "createdBy.username")
    @Mapping(target = "createdAt", source = "createdAt")
    AttachmentResponse toResponse(Attachment attachment);

    List<AttachmentResponse> toResponseList(List<Attachment> attachments);
}
