package dev.muazmemis.finalproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.muazmemis.finalproject.model.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findAllByActiveTrue();

    Optional<Attachment> findByIdAndActiveTrue(Long id);

}
