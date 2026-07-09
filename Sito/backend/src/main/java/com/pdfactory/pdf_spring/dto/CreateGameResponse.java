package com.pdfactory.pdf_spring.dto;

import java.util.UUID;

public record CreateGameResponse(UUID id, String codice, String status) {}