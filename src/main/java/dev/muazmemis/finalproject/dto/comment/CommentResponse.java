package dev.muazmemis.finalproject.dto.comment;

import java.time.LocalDateTime;

public record CommentResponse (
        Long id,
        String content,
        Long taskId,
        Long userId,
        String username,
        LocalDateTime createdAt
) {
} 
