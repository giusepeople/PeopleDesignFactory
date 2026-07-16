package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.InvioModulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvioModuloRepository extends JpaRepository<InvioModulo, UUID> {
    Optional<InvioModulo> findByGruppoIdAndModuloId(UUID gruppoId, UUID moduloId);
    List<InvioModulo> findByModuloId(UUID moduloId);
}