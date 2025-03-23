package dev.muazmemis.finalproject.dto.user;

import dev.muazmemis.finalproject.model.enums.Role;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String username,
        String password,
        Role role,
        boolean active
) {
}
