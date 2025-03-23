package dev.muazmemis.finalproject.controller;

import static dev.muazmemis.finalproject.constant.EndPoints.COMMENT_ENDPOINT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.muazmemis.finalproject.dto.comment.CommentRequest;
import dev.muazmemis.finalproject.dto.comment.CommentResponse;
import dev.muazmemis.finalproject.service.CommentService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CommentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentRequest request;
    private CommentResponse response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        request = new CommentRequest("Test comment", 1L);

        response = new CommentResponse(
                1L,
                "Test comment",
                1L,
                1L,
                "user1",
                LocalDateTime.now());
    }

    @Test
    void saveComment_Success() throws Exception {
        when(commentService.saveComment(any(CommentRequest.class))).thenReturn(response);

        mockMvc.perform(post(COMMENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test comment"));
    }

    @Test
    void getAllComments_Success() throws Exception {
        when(commentService.getAllComments()).thenReturn(List.of(response));

        mockMvc.perform(get(COMMENT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Test comment"));
    }

    @Test
    void getCommentById_Success() throws Exception {
        when(commentService.getCommentById(1L)).thenReturn(response);

        mockMvc.perform(get(COMMENT_ENDPOINT + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test comment"));
    }

    @Test
    void deleteComment_Success() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete(COMMENT_ENDPOINT + "/1"))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(1L);
    }
}
