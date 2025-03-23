package dev.muazmemis.finalproject.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import dev.muazmemis.finalproject.model.entity.User;

@Configuration
public class AuditorAwareConfig {

    @Bean
    public AuditorAware<User> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.empty();
            }

            return Optional.of((User) authentication.getPrincipal());
        };
    }
}
