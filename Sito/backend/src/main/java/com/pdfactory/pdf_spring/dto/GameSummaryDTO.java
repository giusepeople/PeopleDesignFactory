package com.pdfactory.pdf_spring.dto;

import java.time.Instant;
import java.util.UUID;


public record GameSummaryDTO(
        UUID id,
        String codice,
        String status,
        Instant createdAt,
        Integer totaleGiocatori,
        Integer totaleGruppi
) {}