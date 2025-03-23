package dev.muazmemis.finalproject.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import dev.muazmemis.finalproject.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import dev.muazmemis.finalproject.dto.attachment.AttachmentRequest;
import dev.muazmemis.finalproject.dto.attachment.AttachmentResponse;
import dev.muazmemis.finalproject.mapper.AttachmentMapper;
import dev.muazmemis.finalproject.model.entity.Attachment;
import dev.muazmemis.finalproject.model.entity.Task;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.model.enums.Role;
import dev.muazmemis.finalproject.repository.AttachmentRepository;
import dev.muazmemis.finalproject.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final AttachmentMapper attachmentMapper;
    private final SecurityUtils securityUtils;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public List<AttachmentResponse> uploadAttachment(AttachmentRequest request) throws IOException {
        Task task = taskRepository.findByIdAndActiveTrue(request.taskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + request.taskId()));

        List<Attachment> attachments = saveAttachmentFiles(request.files());
        attachments.forEach(attachment -> attachment.setTask(task));

        List<AttachmentResponse> responses = attachmentMapper.toResponseList(
                attachmentRepository.saveAll(attachments));

        log.info("{} attachments uploaded for task ID: {}", responses.size(), request.taskId());
        return responses;
    }

    public List<Attachment> saveAttachmentFiles(List<MultipartFile> files) throws IOException {
        List<Attachment> attachments = new ArrayList<>();
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        for (MultipartFile file : files) {
            if (file.isEmpty())
                continue;

            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileExtension = StringUtils.getFilenameExtension(fileName);
            String uniqueFileName = UUID.randomUUID() + "." + (fileExtension != null ? fileExtension : "bin");

            try {
                Path targetLocation = uploadPath.resolve(uniqueFileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                attachments.add(Attachment.builder()
                        .fileName(fileName)
                        .filePath(targetLocation.toString())
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .active(true)
                        .build());

                log.info("Saved file: {}", fileName);
            } catch (IOException e) {
                log.error("Failed to save file: {}", fileName, e);
                throw e;
            }
        }

        return attachments;
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAllAttachments() {
        log.info("Attachments retrieved");
        return attachmentMapper.toResponseList(attachmentRepository.findAllByActiveTrue());
    }

    @Transactional(readOnly = true)
    public AttachmentResponse getByAttachmentId(Long id) {
        Attachment attachment = attachmentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + id));

        log.info("Attachment retrieved with id: " + id);
        return attachmentMapper.toResponse(attachment);
    }

    @Transactional
    public void deleteAttachment(Long id) throws IOException {
        Attachment attachment = attachmentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + id));

        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.getRole().equals(Role.TEAM_MEMBER) &&
                !attachment.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own attachments");
        }

        Path filePath = Paths.get(attachment.getFilePath());
        Files.deleteIfExists(filePath);
        attachment.setActive(false);

        log.info("Attachment deleted");
        attachmentRepository.save(attachment);
    }

}
