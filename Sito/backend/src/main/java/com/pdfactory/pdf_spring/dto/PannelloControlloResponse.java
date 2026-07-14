package com.pdfactory.pdf_spring.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PannelloControlloResponse(
        UUID id,
        String codice,
        String status,
        Integer totaleGiocatori,
        FaseCorrenteDTO faseAttuale,
        Instant faseIniziataIl,
        List<GruppoDettaglioDTO> gruppi,
        List<GiocatoreDettaglioDTO> giocatoriSenzaGruppo,
        boolean tuttiGruppiPronti
) {}