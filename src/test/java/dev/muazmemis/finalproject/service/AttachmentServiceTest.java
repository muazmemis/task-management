package dev.muazmemis.finalproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import dev.muazmemis.finalproject.dto.attachment.AttachmentRequest;
import dev.muazmemis.finalproject.dto.attachment.AttachmentResponse;
import dev.muazmemis.finalproject.mapper.AttachmentMapper;
import dev.muazmemis.finalproject.model.entity.Attachment;
import dev.muazmemis.finalproject.model.entity.Task;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.model.enums.Role;
import dev.muazmemis.finalproject.repository.AttachmentRepository;
import dev.muazmemis.finalproject.repository.TaskRepository;
import dev.muazmemis.finalproject.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

        @Mock
        private AttachmentRepository attachmentRepository;

        @Mock
        private TaskRepository taskRepository;

        @Mock
        private AttachmentMapper attachmentMapper;

        @Mock
        private SecurityUtils securityUtils;

        @InjectMocks
        private AttachmentService attachmentService;

        @TempDir
        Path tempDir;

        private Task task;
        private Attachment attachment;
        private AttachmentRequest request;
        private AttachmentResponse response;
        private User user;

        @BeforeEach
        void setUp() {
                task = Task.builder()
                                .id(1L)
                                .title("Test Task")
                                .active(true)
                                .build();

                user = User.builder()
                                .id(1L)
                                .username("user")
                                .role(Role.PROJECT_MANAGER)
                                .active(true)
                                .build();

                attachment = Attachment.builder()
                                .id(1L)
                                .fileName("test.txt")
                                .filePath(tempDir.resolve("test.txt").toString())
                                .fileType("text/plain")
                                .fileSize(1024L)
                                .task(task)
                                .createdBy(user)
                                .active(true)
                                .build();

                MockMultipartFile mockFile = new MockMultipartFile(
                                "files",
                                "test.txt",
                                "text/plain",
                                "test content".getBytes());

                request = new AttachmentRequest(
                                List.of(mockFile),
                                1L);

                LocalDateTime now = LocalDateTime.now();

                response = new AttachmentResponse(
                                1L,
                                "test.txt",
                                tempDir.resolve("test.txt").toString(),
                                "text/plain",
                                1024L,
                                1L,
                                1L,
                                "user",
                                now,
                                true);

                ReflectionTestUtils.setField(attachmentService, "uploadDir", tempDir.toString());

                lenient().when(securityUtils.getCurrentUser()).thenReturn(user);
        }

        @Test
        void uploadAttachment_Success() throws IOException {
                when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(task));
                when(attachmentRepository.saveAll(any())).thenReturn(List.of(attachment));
                when(attachmentMapper.toResponseList(any())).thenReturn(List.of(response));

                List<AttachmentResponse> result = attachmentService.uploadAttachment(request);

                assertNotNull(result);
                assertThat(result).hasSize(1);
                assertEquals("test.txt", result.getFirst().fileName());
        }

        @Test
        void uploadAttachment_TaskNotFound_ThrowsException() {
                when(taskRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> attachmentService.uploadAttachment(request))
                                .isInstanceOf(EntityNotFoundException.class)
                                .hasMessageContaining("Task not found");
        }

        @Test
        void getAllAttachments_Success() {
                List<Attachment> attachments = List.of(attachment);
                List<AttachmentResponse> responses = List.of(response);

                when(attachmentRepository.findAllByActiveTrue()).thenReturn(attachments);
                when(attachmentMapper.toResponseList(attachments)).thenReturn(responses);

                List<AttachmentResponse> result = attachmentService.getAllAttachments();

                assertNotNull(result);
                assertThat(result).hasSize(1);
        }

        @Test
        void getByAttachmentId_Success() {
                when(attachmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(attachment));
                when(attachmentMapper.toResponse(attachment)).thenReturn(response);

                AttachmentResponse result = attachmentService.getByAttachmentId(1L);

                assertNotNull(result);
                assertEquals(1L, result.id());
        }

        @Test
        void getByAttachmentId_NotFound_ThrowsException() {
                when(attachmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> attachmentService.getByAttachmentId(1L))
                                .isInstanceOf(EntityNotFoundException.class)
                                .hasMessageContaining("not found");
        }

        @Test
        void deleteAttachment_Success() throws IOException {
                when(attachmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(attachment));
                when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
                when(securityUtils.getCurrentUser()).thenReturn(user);

                attachmentService.deleteAttachment(1L);

                assertFalse(attachment.isActive());
                verify(attachmentRepository).save(attachment);
        }

        @Test
        void deleteAttachment_UnauthorizedUser_ThrowsException() throws IOException {
                User teamMember = User.builder()
                                .id(2L)
                                .username("team")
                                .role(Role.TEAM_MEMBER)
                                .active(true)
                                .build();

                User createdBy = User.builder()
                                .id(3L)
                                .username("other")
                                .role(Role.TEAM_MEMBER)
                                .active(true)
                                .build();

                attachment.setCreatedBy(createdBy);

                when(securityUtils.getCurrentUser()).thenReturn(teamMember);
                when(attachmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(attachment));

                assertThatThrownBy(() -> attachmentService.deleteAttachment(1L))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("You can only delete your own attachments");
        }

        @Test
        void deleteAttachment_NotFound_ThrowsException() {
                when(attachmentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> attachmentService.deleteAttachment(1L))
                                .isInstanceOf(EntityNotFoundException.class)
                                .hasMessageContaining("not found");
        }
}
