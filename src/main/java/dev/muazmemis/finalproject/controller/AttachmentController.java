package dev.muazmemis.finalproject.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.muazmemis.finalproject.dto.attachment.AttachmentRequest;
import dev.muazmemis.finalproject.dto.attachment.AttachmentResponse;
import dev.muazmemis.finalproject.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachment", description = "Attachment management APIs")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Upload a new attachment", description = "Upload a new attachment to a task. Accessible by roles: PROJECT_MANAGER, TEAM_LEADER, TEAM_MEMBER.")
    public ResponseEntity<List<AttachmentResponse>> uploadAttachment(@ModelAttribute AttachmentRequest request) throws IOException {
        return ResponseEntity.ok(attachmentService.uploadAttachment(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Get all attachments", description = "Get a list of all attachments")
    public ResponseEntity<List<AttachmentResponse>> getAllAttachments() {
        return ResponseEntity.ok(attachmentService.getAllAttachments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Get an attachment by ID", description = "Get an attachment's details by its ID")
    public ResponseEntity<AttachmentResponse> getByAttachmentId(@PathVariable Long id) {
        return ResponseEntity.ok(attachmentService.getByAttachmentId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Delete an attachment", description = "Delete an attachment by its ID")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) throws IOException {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }

}
