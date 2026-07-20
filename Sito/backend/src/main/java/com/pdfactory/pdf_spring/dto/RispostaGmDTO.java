package com.pdfactory.pdf_spring.dto;

import java.util.UUID;

public record RispostaGmDTO(
        UUID domandaId,
        Integer orderIndex,
        String domandaTesto,
        String tipo,
        String testoRisposta,
        String giustificazione,
        Boolean corretta,
        String opzioneCorretta,
        String hintText,
        String rispostoDaNickname
) {}