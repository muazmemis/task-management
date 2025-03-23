package dev.muazmemis.finalproject.dto.department;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest (
        @NotBlank(message = "Department name is required")
        String name
){
}
