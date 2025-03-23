package dev.muazmemis.finalproject.service;

import dev.muazmemis.finalproject.constant.ErrorMessages;
import dev.muazmemis.finalproject.dto.user.UserRequest;
import dev.muazmemis.finalproject.dto.user.UserResponse;
import dev.muazmemis.finalproject.dto.user.UserUpdateRequest;
import dev.muazmemis.finalproject.exception.UserNameAlreadyExistException;
import dev.muazmemis.finalproject.mapper.UserMapper;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse saveUser(UserRequest request) {
        if (userRepository.existsByUsernameAndActiveTrue(request.username())) {
            throw new IllegalStateException("User with username " + request.username() + " already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        log.info("User saved: {}", user.getUsername());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse findByUserId(Long id) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        log.info("User found: {}", user.getUsername());
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllActiveUsers() {
        log.info("Find all active users");
        return userMapper.toResponseList(userRepository.findAllByActiveTrue());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {
        log.info("Find all users");
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, id)));

        if (userRepository.existsByUsernameAndActiveTrue(request.username()) && !user.getUsername().equals(request.username()))
            throw new UserNameAlreadyExistException(request.username());

        User userUpdate = userMapper.toEntity(request);
        userUpdate.setId(id);
        userUpdate.setPassword(passwordEncoder.encode(request.password()));

        log.info("User updated: {}", userUpdate.getUsername());
        return userMapper.toResponse(userRepository.save(userUpdate));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, id)));

        user.setActive(false);

        log.info("User deleted: {}", user.getUsername());
        userRepository.save(user);
    }
}
