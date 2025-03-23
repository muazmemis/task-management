package dev.muazmemis.finalproject.dto.task;

import dev.muazmemis.finalproject.model.enums.TaskState;
import jakarta.validation.constraints.NotNull;

public record TaskStateUpdateRequest (
        @NotNull(message = "State is required")
        TaskState state,
        String reason
) {
}
