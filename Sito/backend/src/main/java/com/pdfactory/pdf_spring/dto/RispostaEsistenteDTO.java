package com.pdfactory.pdf_spring.dto;

import java.util.UUID;

public record RispostaEsistenteDTO(
        UUID domandaId,
        String testoRisposta,       // null se il chiamante non è autorizzato a vederla
        String giustificazione,     // idem
        boolean rispostaPresente,   // true/false SEMPRE visibile, senza contenuto
        Boolean corretta,           // null se non autorizzato
        String hintDaMostrare       // valorizzato solo se corretta == false
) {}