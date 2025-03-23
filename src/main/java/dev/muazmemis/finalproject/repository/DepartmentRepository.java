package dev.muazmemis.finalproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.muazmemis.finalproject.model.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);

    List<Department> findAllByActiveTrue();

    Optional<Department> findByIdAndActiveTrue(Long id);

    boolean existsByNameAndActiveTrue(String name);
}
