package com.pdfactory.pdf_spring.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Riga di riepilogo per la lista partite del Game Master.
 * Sostituisce CreateGameResponse in GameController#getMyGames, che da solo
 * non bastava più a popolare le card della dashboard (mancavano data di
 * creazione, numero giocatori e numero gruppi).
 */
public record GameSummaryDTO(
        UUID id,
        String codice,
        String status,
        Instant createdAt,
        Integer totaleGiocatori,
        Integer totaleGruppi
) {}