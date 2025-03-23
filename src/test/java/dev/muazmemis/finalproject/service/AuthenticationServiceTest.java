package dev.muazmemis.finalproject.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.muazmemis.finalproject.constant.ErrorMessages;
import dev.muazmemis.finalproject.dto.auth.LoginRequest;
import dev.muazmemis.finalproject.dto.auth.LoginResponse;
import dev.muazmemis.finalproject.dto.auth.RegisterRequest;
import dev.muazmemis.finalproject.exception.UserNameAlreadyExistException;
import dev.muazmemis.finalproject.mapper.UserMapper;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.model.enums.Role;
import dev.muazmemis.finalproject.repository.UserRepository;
import dev.muazmemis.finalproject.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.TEAM_MEMBER)
                .active(true)
                .build();

        registerRequest = new RegisterRequest(
                "Test",
                "User",
                "testuser",
                "password",
                Role.TEAM_MEMBER);

        loginRequest = new LoginRequest(
                "testuser",
                "password");

        jwtToken = "jwt.token.generated";
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsernameAndActiveTrue(registerRequest.username())).thenReturn(false);
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        LoginResponse response = authenticationService.register(registerRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.token());
        verify(userRepository).save(user);
    }

    @Test
    void register_UserNameExists_ThrowsException() {
        when(userRepository.existsByUsernameAndActiveTrue(registerRequest.username())).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(UserNameAlreadyExistException.class)
                .hasMessageContaining(registerRequest.username());
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsernameAndActiveTrue(loginRequest.username())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        LoginResponse response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.token());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()));
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsernameAndActiveTrue(loginRequest.username())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.login(loginRequest))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(String.format(ErrorMessages.USERNAME_NOT_FOUND, loginRequest.username()));
    }
}
