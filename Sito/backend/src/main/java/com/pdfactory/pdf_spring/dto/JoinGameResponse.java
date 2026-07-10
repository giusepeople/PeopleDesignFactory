package com.pdfactory.pdf_spring.dto;

import java.util.UUID;

public record JoinGameResponse(UUID giocatoreId, UUID partitaId, String sessionToken, String nickname) {}