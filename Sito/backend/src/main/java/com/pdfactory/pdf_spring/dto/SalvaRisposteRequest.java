package com.pdfactory.pdf_spring.dto;

import java.util.List;
import java.util.UUID;

public record SalvaRisposteRequest(UUID giocatoreId, String sessionToken, List<RispostaInputDTO> risposte) {}