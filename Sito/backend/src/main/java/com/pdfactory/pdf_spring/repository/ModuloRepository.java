package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ModuloRepository extends JpaRepository<Modulo, UUID> {
    Optional<Modulo> findByFaseId(UUID faseId);
}