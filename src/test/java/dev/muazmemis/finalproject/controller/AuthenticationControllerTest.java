package dev.muazmemis.finalproject.controller;

import static dev.muazmemis.finalproject.constant.EndPoints.AUTH_ENDPOINT;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import dev.muazmemis.finalproject.dto.auth.LoginRequest;
import dev.muazmemis.finalproject.dto.auth.LoginResponse;
import dev.muazmemis.finalproject.dto.auth.RegisterRequest;
import dev.muazmemis.finalproject.model.enums.Role;
import dev.muazmemis.finalproject.service.AuthenticationService;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Test",
                "User",
                "testuser",
                "password",
                Role.TEAM_MEMBER);

        LoginResponse response = new LoginResponse("jwt.token.generated");

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post(AUTH_ENDPOINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("jwt.token.generated")));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest(
                "testuser",
                "password");

        LoginResponse response = new LoginResponse("jwt.token.generated");

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post(AUTH_ENDPOINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("jwt.token.generated")));
    }
}
