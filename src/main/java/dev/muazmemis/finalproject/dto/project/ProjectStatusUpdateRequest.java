package dev.muazmemis.finalproject.dto.project;

import dev.muazmemis.finalproject.model.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record ProjectStatusUpdateRequest(
        @NotNull(message = "Status is required")
        ProjectStatus status
) {
}
