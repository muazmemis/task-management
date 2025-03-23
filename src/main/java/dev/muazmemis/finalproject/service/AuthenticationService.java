package dev.muazmemis.finalproject.service;

import dev.muazmemis.finalproject.constant.ErrorMessages;
import dev.muazmemis.finalproject.exception.UserNameAlreadyExistException;
import dev.muazmemis.finalproject.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.muazmemis.finalproject.dto.auth.LoginRequest;
import dev.muazmemis.finalproject.dto.auth.LoginResponse;
import dev.muazmemis.finalproject.dto.auth.RegisterRequest;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.repository.UserRepository;
import dev.muazmemis.finalproject.security.JwtService;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public LoginResponse register(RegisterRequest request) {

        if (userRepository.existsByUsernameAndActiveTrue(request.username()))
            throw new UserNameAlreadyExistException(request.username());

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        log.info("User {} registered", user.getUsername());
        return new LoginResponse(jwtService.generateToken(user));
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameAndActiveTrue(request.username())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(ErrorMessages.USERNAME_NOT_FOUND, request.username())));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()));

        log.info("User {} logged in", user.getUsername());
        return new LoginResponse(jwtService.generateToken(user));
    }
}
