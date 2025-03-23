package dev.muazmemis.finalproject.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest (
        @NotBlank(message = "Content is required")
        String content,

        @NotNull(message = "Task ID is required")
        Long taskId
) {
}
