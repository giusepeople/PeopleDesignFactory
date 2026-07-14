package com.pdfactory.pdf_spring.dto;

import java.util.List;
import java.util.UUID;

public record LobbyStateResponse(UUID partitaId, String codice, String status, Integer totaleGiocatori,
                                 List<GiocatoreLobbyDTO> giocatori, List<GruppoStatoDTO> gruppi) {}