package com.pdfactory.pdf_spring.dto;
import java.util.List;
import java.util.UUID;
public record ModuloSummary(UUID id, String titolo, List<DomandaSummary> domande) {}