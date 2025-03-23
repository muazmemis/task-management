package dev.muazmemis.finalproject.dto.project;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record TeamMembersRequest (
        @NotEmpty(message = "User IDs cannot be empty")
        List<Long> userIds
) {
}
