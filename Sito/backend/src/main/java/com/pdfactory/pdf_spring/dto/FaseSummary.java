package com.pdfactory.pdf_spring.dto;
import java.util.UUID;
public record FaseSummary(UUID id, Integer ordinal, String nome, String tipo, Integer durataMinuti, ModuloSummary modulo) {}