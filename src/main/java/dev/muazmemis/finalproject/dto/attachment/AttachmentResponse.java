package dev.muazmemis.finalproject.dto.attachment;

import java.time.LocalDateTime;

public record AttachmentResponse(
        Long id,
        String fileName,
        String filePath,
        String fileType,
        Long fileSize,
        Long taskId,
        Long userId,
        String username,
        LocalDateTime createdAt,
        boolean active
) {
}
