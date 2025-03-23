package dev.muazmemis.finalproject.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.muazmemis.finalproject.dto.comment.CommentRequest;
import dev.muazmemis.finalproject.dto.comment.CommentResponse;
import dev.muazmemis.finalproject.mapper.CommentMapper;
import dev.muazmemis.finalproject.model.entity.Comment;
import dev.muazmemis.finalproject.model.entity.Task;
import dev.muazmemis.finalproject.repository.CommentRepository;
import dev.muazmemis.finalproject.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse saveComment(CommentRequest request) {
        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + request.taskId()));

        Comment comment = commentMapper.toEntity(request);
        comment.setTask(task);

        log.info("Comment saved: {}", comment.getContent());
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));

        log.info("Comment found: {}", comment.getContent());
        return commentMapper.toResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getAllComments() {
        List<Comment> comments = commentRepository.findAllByActiveTrue();

        log.info("Comments found: {}", comments.size());
        return commentMapper.toResponseList(comments);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));

        comment.setActive(false);

        log.info("Comment deleted: {}", comment.getContent());
        commentRepository.save(comment);
    }
}
