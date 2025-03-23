package dev.muazmemis.finalproject.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.muazmemis.finalproject.dto.comment.CommentRequest;
import dev.muazmemis.finalproject.dto.comment.CommentResponse;
import dev.muazmemis.finalproject.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comment management APIs")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Create a new comment", description = "Create a new comment with the provided details")
    public ResponseEntity<CommentResponse> saveComment(@RequestBody @Valid CommentRequest request) {
        return ResponseEntity.ok(commentService.saveComment(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Get a comment by ID", description = "Get a comment's details by its ID")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER', 'TEAM_MEMBER')")
    @Operation(summary = "Get all comments", description = "Get a list of all comments")
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'TEAM_LEADER')")
    @Operation(summary = "Delete a comment", description = "Delete a comment by its ID")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
