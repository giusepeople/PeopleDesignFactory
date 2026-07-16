package com.pdfactory.pdf_spring.repository;

import com.pdfactory.pdf_spring.model.Domanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DomandaRepository extends JpaRepository<Domanda, UUID> {
}