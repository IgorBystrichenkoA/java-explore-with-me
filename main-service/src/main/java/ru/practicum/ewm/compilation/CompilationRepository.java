package ru.practicum.ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Optional<Compilation> getCompilationById(Long id);
}
