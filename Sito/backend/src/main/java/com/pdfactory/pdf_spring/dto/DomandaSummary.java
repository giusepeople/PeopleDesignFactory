package com.pdfactory.pdf_spring.dto;
import java.util.UUID;
public record DomandaSummary(UUID id, String type, String text, boolean rispostaRistretta) {}