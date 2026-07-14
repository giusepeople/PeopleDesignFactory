package com.pdfactory.pdf_spring.dto;

import java.util.UUID;

public record MarcaProntoRequest(UUID giocatoreId, String sessionToken) {}