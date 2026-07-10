package com.pdfactory.pdf_spring.dto;

import java.util.List;
import java.util.UUID;

public record GruppoDettaglioDTO(UUID id, Integer teamNum, String stato, List<GiocatoreDettaglioDTO> giocatori) {}