package dev.muazmemis.finalproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.muazmemis.finalproject.constant.ErrorMessages;
import dev.muazmemis.finalproject.dto.user.UserRequest;
import dev.muazmemis.finalproject.dto.user.UserResponse;
import dev.muazmemis.finalproject.dto.user.UserUpdateRequest;
import dev.muazmemis.finalproject.exception.UserNameAlreadyExistException;
import dev.muazmemis.finalproject.mapper.UserMapper;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.model.enums.Role;
import dev.muazmemis.finalproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;
    private UserUpdateRequest userUpdateRequest;

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

        userRequest = new UserRequest(
                "Test",
                "User",
                "testuser",
                "password",
                Role.TEAM_MEMBER);

        userResponse = new UserResponse(
                1L,
                "Test",
                "User",
                "testuser",
                "***",
                Role.TEAM_MEMBER,
                true);

        userUpdateRequest = new UserUpdateRequest(
                "Test",
                "User",
                "testuser",
                "newpassword",
                Role.TEAM_MEMBER,
                true);
    }

    @Test
    void saveUser_Success() {
        when(userRepository.existsByUsernameAndActiveTrue(userRequest.username())).thenReturn(false);
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(passwordEncoder.encode(userRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.saveUser(userRequest);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(userRequest.username());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void saveUser_UserNameExists_ThrowsException() {
        when(userRepository.existsByUsernameAndActiveTrue(userRequest.username())).thenReturn(true);

        assertThatThrownBy(() -> userService.saveUser(userRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User with username " + userRequest.username() + " already exists");
    }

    @Test
    void findByUserId_Success() {
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.findByUserId(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findByUserId_NotFound_ThrowsException() {
        when(userRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUserId(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id: " + 1L);
    }

    @Test
    void findAllActiveUsers_Success() {
        List<User> users = List.of(user);
        List<UserResponse> responses = List.of(userResponse);

        when(userRepository.findAllByActiveTrue()).thenReturn(users);
        when(userMapper.toResponseList(users)).thenReturn(responses);

        List<UserResponse> result = userService.findAllActiveUsers();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().username()).isEqualTo("testuser");
    }

    @Test
    void findAllUsers_Success() {
        List<User> users = List.of(user);
        List<UserResponse> responses = List.of(userResponse);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseList(users)).thenReturn(responses);

        List<UserResponse> result = userService.findAllUsers();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().username()).isEqualTo("testuser");
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsernameAndActiveTrue(userUpdateRequest.username())).thenReturn(false);
        when(userMapper.toEntity(userUpdateRequest)).thenReturn(user);
        when(passwordEncoder.encode(userUpdateRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser(1L, userUpdateRequest);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(userUpdateRequest.username());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, userUpdateRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format(ErrorMessages.USER_NOT_FOUND, 1L));
    }

    @Test
    void updateUser_UsernameExists_ThrowsException() {
        User existingUser = User.builder()
                .id(1L)
                .username("otheruser")
                .password("password")
                .firstName("Other")
                .lastName("User")
                .role(Role.TEAM_MEMBER)
                .active(true)
                .build();

        UserUpdateRequest request = new UserUpdateRequest(
                "Test",
                "User",
                "testuser",
                "newpassword",
                Role.TEAM_MEMBER,
                true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsernameAndActiveTrue(request.username())).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(UserNameAlreadyExistException.class)
                .hasMessageContaining(request.username());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format(ErrorMessages.USER_NOT_FOUND, 1L));
    }
}
