package dev.muazmemis.finalproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.muazmemis.finalproject.model.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByDepartmentId(Long departmentId);

    List<Project> findAllByActiveTrue();

    Optional<Project> findByIdAndActiveTrue(Long id);

    List<Project> findByDepartmentIdAndActiveTrue(Long departmentId);

}
