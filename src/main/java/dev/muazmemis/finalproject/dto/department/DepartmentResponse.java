package dev.muazmemis.finalproject.dto.department;

import java.util.List;

public record DepartmentResponse (
        Long id,
        String name,
        List<Long> projectIds
) {
} 
