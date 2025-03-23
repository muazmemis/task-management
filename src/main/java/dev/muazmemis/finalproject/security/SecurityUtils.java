package dev.muazmemis.finalproject.security;

import dev.muazmemis.finalproject.constant.ErrorMessages;
import dev.muazmemis.finalproject.model.entity.User;
import dev.muazmemis.finalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        return userRepository.findByUsernameAndActiveTrue(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(ErrorMessages.USERNAME_NOT_FOUND, authentication.getName())));
    }
}
