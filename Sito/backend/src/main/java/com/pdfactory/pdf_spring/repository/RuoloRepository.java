package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RuoloRepository extends JpaRepository<Ruolo, UUID> {
    Optional<Ruolo> findByCodice(String codice);
}