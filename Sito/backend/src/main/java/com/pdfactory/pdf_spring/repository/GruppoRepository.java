package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.Gruppo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GruppoRepository extends JpaRepository<Gruppo, UUID> {
    List<Gruppo> findByPartitaIdOrderByTeamNumAsc(UUID partitaId);
}