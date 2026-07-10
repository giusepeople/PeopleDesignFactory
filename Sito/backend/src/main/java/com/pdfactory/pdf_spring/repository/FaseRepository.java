package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.Fase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FaseRepository extends JpaRepository<Fase, UUID> {
    List<Fase> findAllByOrderByOrdinalAsc();
}