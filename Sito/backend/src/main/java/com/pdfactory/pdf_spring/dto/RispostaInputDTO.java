package com.pdfactory.pdf_spring.dto;

import java.util.UUID;

public record RispostaInputDTO(UUID domandaId, String testoRisposta, String giustificazione) {}