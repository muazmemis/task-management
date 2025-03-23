package dev.muazmemis.finalproject.dto.project;


import dev.muazmemis.finalproject.model.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ProjectUpdateRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        ProjectStatus status,
        List<Long> teamMemberIds
) {
}
