package com.pdfactory.pdf_spring.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InvioModuloGmDTO(
        UUID invioId,
        UUID gruppoId,
        Integer teamNum,
        String statoGruppo,
        String statoInvio,
        Instant inviatoIl,
        String motivoRifiuto,
        Integer minutiExtra,
        Long secondiRimanenti,
        List<RispostaGmDTO> risposte
) {}