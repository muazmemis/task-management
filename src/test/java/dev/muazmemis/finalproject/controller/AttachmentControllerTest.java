package dev.muazmemis.finalproject.controller;

import static dev.muazmemis.finalproject.constant.EndPoints.ATTACHMENT_ENDPOINT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dev.muazmemis.finalproject.dto.attachment.AttachmentRequest;
import dev.muazmemis.finalproject.dto.attachment.AttachmentResponse;
import dev.muazmemis.finalproject.service.AttachmentService;

@ExtendWith(MockitoExtension.class)
class AttachmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private AttachmentController attachmentController;

    private MockMultipartFile mockFile;
    private AttachmentResponse response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController).build();

        mockFile = new MockMultipartFile("files", "test.txt", "text/plain", "test content".getBytes());

        response = new AttachmentResponse(
                1L,
                "test.txt",
                "/path/to/file",
                "text/plain",
                mockFile.getSize(),
                1L,
                1L,
                "user1",
                LocalDateTime.now(),
                true);
    }

    @Test
    void uploadAttachment_Success() throws Exception {
        when(attachmentService.uploadAttachment(any(AttachmentRequest.class))).thenReturn(List.of(response));

        mockMvc.perform(multipart(ATTACHMENT_ENDPOINT)
                .file(mockFile)
                .param("taskId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fileName").value("test.txt"));
    }

    @Test
    void getAllAttachments_Success() throws Exception {
        when(attachmentService.getAllAttachments()).thenReturn(List.of(response));

        mockMvc.perform(get(ATTACHMENT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fileName").value("test.txt"));
    }

    @Test
    void getByAttachmentId_Success() throws Exception {
        when(attachmentService.getByAttachmentId(1L)).thenReturn(response);

        mockMvc.perform(get(ATTACHMENT_ENDPOINT + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fileName").value("test.txt"));
    }

    @Test
    void deleteAttachment_Success() throws Exception {
        mockMvc.perform(delete(ATTACHMENT_ENDPOINT + "/1"))
                .andExpect(status().isNoContent());

        verify(attachmentService).deleteAttachment(1L);
    }
}
