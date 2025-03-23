package dev.muazmemis.finalproject.dto.project;

import java.util.List;

import dev.muazmemis.finalproject.model.enums.ProjectStatus;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        ProjectStatus status,
        String departmentName,
        List<Long> taskIds,
        List<Long> teamMemberIds
) {

}
