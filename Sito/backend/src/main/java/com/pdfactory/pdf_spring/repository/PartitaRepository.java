package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.Partita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartitaRepository extends JpaRepository<Partita, UUID> {
    Optional<Partita> findByCodPartita(String codPartita);
    List<Partita> findByGameMaster_NomeOrderByCreatedAtDesc(String nome);
}