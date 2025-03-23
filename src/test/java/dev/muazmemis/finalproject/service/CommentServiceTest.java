package dev.muazmemis.finalproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.muazmemis.finalproject.dto.comment.CommentRequest;
import dev.muazmemis.finalproject.dto.comment.CommentResponse;
import dev.muazmemis.finalproject.mapper.CommentMapper;
import dev.muazmemis.finalproject.model.entity.Comment;
import dev.muazmemis.finalproject.model.entity.Task;
import dev.muazmemis.finalproject.repository.CommentRepository;
import dev.muazmemis.finalproject.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private Task task;
    private Comment comment;
    private CommentRequest request;
    private CommentResponse response;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .active(true)
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("This is a test comment")
                .task(task)
                .active(true)
                .build();

        request = new CommentRequest("This is a test comment", 1L);

        LocalDateTime now = LocalDateTime.now();

        response = new CommentResponse(
                1L,
                "This is a test comment",
                1L,
                1L,
                "user",
                now
        );

    }

    @Test
    void saveComment_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(commentMapper.toEntity(request)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(response);

        CommentResponse result = commentService.saveComment(request);

        assertNotNull(result);
        assertEquals(request.content(), result.content());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void saveComment_TaskNotFound_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.saveComment(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void getCommentById_Success() {
        when(commentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toResponse(comment)).thenReturn(response);

        CommentResponse result = commentService.getCommentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.taskId());
    }

    @Test
    void getCommentById_NotFound_ThrowsException() {
        when(commentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getCommentById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getAllComments_Success() {
        List<Comment> comments = List.of(comment);
        List<CommentResponse> responses = List.of(response);

        when(commentRepository.findAllByActiveTrue()).thenReturn(comments);
        when(commentMapper.toResponseList(comments)).thenReturn(responses);

        List<CommentResponse> result = commentService.getAllComments();

        assertNotNull(result);
        assertThat(result).hasSize(1);
    }

    @Test
    void deleteComment_Success() {
        when(commentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        commentService.deleteComment(1L);

        assertFalse(comment.isActive());
        verify(commentRepository).save(comment);
    }

    @Test
    void deleteComment_NotFound_ThrowsException() {
        when(commentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
