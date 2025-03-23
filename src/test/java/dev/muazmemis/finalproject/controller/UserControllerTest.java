package dev.muazmemis.finalproject.controller;

import static dev.muazmemis.finalproject.constant.EndPoints.USER_ENDPOINT;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.muazmemis.finalproject.dto.user.UserRequest;
import dev.muazmemis.finalproject.dto.user.UserResponse;
import dev.muazmemis.finalproject.dto.user.UserUpdateRequest;
import dev.muazmemis.finalproject.model.enums.Role;
import dev.muazmemis.finalproject.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void saveUser_Success() throws Exception {
        // Arrange
        UserRequest request = new UserRequest(
                "Test",
                "User",
                "testuser",
                "password",
                Role.TEAM_MEMBER);

        UserResponse response = new UserResponse(
                1L,
                "Test",
                "User",
                "testuser",
                "***",
                Role.TEAM_MEMBER,
                true);

        when(userService.saveUser(any(UserRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void findByUserId_Success() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Test",
                "User",
                "testuser",
                "***",
                Role.TEAM_MEMBER,
                true);

        when(userService.findByUserId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void findAllActiveUsers_Success() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Test",
                "User",
                "testuser",
                "***",
                Role.TEAM_MEMBER,
                true);

        when(userService.findAllActiveUsers()).thenReturn(List.of(response));

        mockMvc.perform(get(USER_ENDPOINT + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("testuser")));
    }

    @Test
    void findAllUsers_Success() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Test",
                "User",
                "testuser",
                "***",
                Role.TEAM_MEMBER,
                true);

        when(userService.findAllUsers()).thenReturn(List.of(response));

        mockMvc.perform(get(USER_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("testuser")));
    }

    @Test
    void updateUser_Success() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest(
                "Test",
                "User",
                "testuser",
                "newpassword",
                Role.TEAM_MEMBER,
                true);

        UserResponse response = new UserResponse(
                1L,
                "Test",
                "User",
                "testuser",
                "***",
                Role.TEAM_MEMBER,
                true);

        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put(USER_ENDPOINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete(USER_ENDPOINT + "/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }
}
