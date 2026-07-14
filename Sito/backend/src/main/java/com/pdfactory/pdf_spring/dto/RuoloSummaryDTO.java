package com.pdfactory.pdf_spring.dto;

import java.util.UUID;

public record RuoloSummaryDTO(
        UUID id,
        String codice,
        String nome,
        String missione,
        String competenze,
        String superpoteri,
        String puntiCritici
) {}