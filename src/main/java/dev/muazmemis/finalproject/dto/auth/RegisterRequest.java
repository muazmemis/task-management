package dev.muazmemis.finalproject.dto.auth;

import dev.muazmemis.finalproject.model.enums.Role;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest (
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password,

        Role role
) {
}
