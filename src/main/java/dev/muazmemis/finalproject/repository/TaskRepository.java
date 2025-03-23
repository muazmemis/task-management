package dev.muazmemis.finalproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.muazmemis.finalproject.model.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.active = true")
    List<Task> findAllActiveTasks();

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.active = true")
    Optional<Task> findByIdAndActiveTrue(@Param("id") Long id);
}
