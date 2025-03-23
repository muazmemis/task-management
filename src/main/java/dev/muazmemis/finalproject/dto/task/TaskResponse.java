package dev.muazmemis.finalproject.dto.task;

import java.util.List;

import dev.muazmemis.finalproject.model.enums.TaskPriority;
import dev.muazmemis.finalproject.model.enums.TaskState;

public record TaskResponse (
        Long id,
        String title,
        String userStory,
        String acceptanceCriteria,
        TaskState state,
        TaskPriority priority,
        String stateChangeReason,
        Long projectId,
        Long assigneeId,
        List<Long> commentIds,
        List<Long> attachmentIds
) {
}
