package dev.muazmemis.finalproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.muazmemis.finalproject.model.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByActiveTrue();

    Optional<Comment> findByIdAndActiveTrue(Long id);

}
