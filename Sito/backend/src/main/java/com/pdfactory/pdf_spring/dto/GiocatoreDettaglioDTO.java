package com.pdfactory.pdf_spring.dto;

import java.util.UUID;

public record GiocatoreDettaglioDTO(UUID id, String nickname, String ruoloNome, String ruoloCodice) {}