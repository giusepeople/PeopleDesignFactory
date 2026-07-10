package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.Giocatore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GiocatoreRepository extends JpaRepository<Giocatore, UUID> {
    List<Giocatore> findByPartitaId(UUID partitaId);
    Optional<Giocatore> findByPartitaIdAndNicknameIgnoreCase(UUID partitaId, String nickname);
    Optional<Giocatore> findBySessionToken(String sessionToken);
}