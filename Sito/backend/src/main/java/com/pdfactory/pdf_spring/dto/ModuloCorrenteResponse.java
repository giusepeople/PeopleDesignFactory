package com.pdfactory.pdf_spring.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ModuloCorrenteResponse(
        UUID moduloId,
        String titolo,
        List<DomandaModuloDTO> domande,
        List<RispostaEsistenteDTO> risposteAttuali,
        String invioStato,       // BOZZA | INVIATO | APPROVATO | RIFIUTATO
        String motivoRifiuto,
        Integer minutiExtra,
        boolean sonoIoPM,
        String mioRuoloCodice,
        Long secondiRimanenti,
        Instant serverTimestamp
) {}