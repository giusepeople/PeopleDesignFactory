package com.pdfactory.pdf_spring.dto;

import java.time.Instant;
import java.util.List;

public record FaseCorrenteResponse(
        String partitaStatus,
        FaseCorrenteDTO fase,
        Instant faseIniziataIl,
        Long secondiRimanenti,
        String contenutoTesto,
        List<DatoBriefingDTO> dati,
        List<OpzioneLivelloDTO> opzioni,
        boolean haComplicazione,
        boolean complicazioneVisibile,
        String complicazioneTesto,
        Instant serverTimestamp
) {}