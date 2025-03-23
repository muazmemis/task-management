package dev.muazmemis.finalproject.dto.task;

import dev.muazmemis.finalproject.model.enums.TaskPriority;
import dev.muazmemis.finalproject.model.enums.TaskState;
import jakarta.validation.constraints.NotBlank;

public record TaskUpdateRequest (
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "User story is required")
        String userStory,

        @NotBlank(message = "Acceptance criteria is required")
        String acceptanceCriteria,

        TaskState state,
        TaskPriority priority,
        String stateChangeReason,
        Long assigneeId
) {
}
