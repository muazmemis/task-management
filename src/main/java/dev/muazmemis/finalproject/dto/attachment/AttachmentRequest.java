package dev.muazmemis.finalproject.dto.attachment;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AttachmentRequest(
        @NotNull(message = "File is required")
        List<MultipartFile> files,

        @NotNull(message = "Task ID is required")
        Long taskId
) {
}
