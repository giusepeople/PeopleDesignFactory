package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.Risposta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RispostaRepository extends JpaRepository<Risposta, UUID> {
    Optional<Risposta> findByInvioModuloIdAndDomandaId(UUID invioModuloId, UUID domandaId);
}