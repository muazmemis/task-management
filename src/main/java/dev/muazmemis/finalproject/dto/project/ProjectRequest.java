package dev.muazmemis.finalproject.dto.project;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")

        String description,

        @NotNull(message = "Department ID is required")
        Long departmentId,

        List<Long> teamMemberIds
) {
}
