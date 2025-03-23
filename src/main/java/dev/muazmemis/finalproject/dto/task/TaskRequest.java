package dev.muazmemis.finalproject.dto.task;

import dev.muazmemis.finalproject.model.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record TaskRequest (
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "User story is required")
        String userStory,

        @NotBlank(message = "Acceptance criteria is required")
        String acceptanceCriteria,

        @NotNull(message = "Priority is required")
        TaskPriority priority,

        @NotNull(message = "Project ID is required")
        Long projectId,

        Long assigneeId,

        List<MultipartFile> files
) {
}
