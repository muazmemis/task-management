package dev.muazmemis.finalproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.muazmemis.finalproject.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndActiveTrue(String username);

    boolean existsByUsernameAndActiveTrue(String username);

    List<User> findAllByActiveTrue();

    Optional<User> findByIdAndActiveTrue(Long id);

}
