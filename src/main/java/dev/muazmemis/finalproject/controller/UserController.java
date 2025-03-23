package dev.muazmemis.finalproject.controller;

import dev.muazmemis.finalproject.dto.user.UserRequest;
import dev.muazmemis.finalproject.dto.user.UserResponse;
import dev.muazmemis.finalproject.dto.user.UserUpdateRequest;
import dev.muazmemis.finalproject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Create a new user", description = "Create a new user with the provided details")
    public ResponseEntity<dev.muazmemis.finalproject.dto.user.UserResponse> saveUser(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(userService.saveUser(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Get a user by ID", description = "Get a user's details by its ID")
    public ResponseEntity<UserResponse> findByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findByUserId(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Get all active users", description = "Get a list of all users")
    public ResponseEntity<List<UserResponse>> findAllActiveUsers() {
        return ResponseEntity.ok(userService.findAllActiveUsers());
    }

    @GetMapping
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Get all users", description = "Get a list of all users")
    public ResponseEntity<List<UserResponse>> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Update a user", description = "Update a user's details by its ID")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Delete a user", description = "Delete a user by its ID")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
