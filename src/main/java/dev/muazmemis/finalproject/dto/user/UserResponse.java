package dev.muazmemis.finalproject.dto.user;

import dev.muazmemis.finalproject.model.enums.Role;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String username,
        String password,
        Role role,
        Boolean active
) {
}