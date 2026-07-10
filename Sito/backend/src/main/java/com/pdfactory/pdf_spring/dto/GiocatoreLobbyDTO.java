package com.pdfactory.pdf_spring.dto;

import java.util.UUID;

public record GiocatoreLobbyDTO(UUID id, String nickname, Integer gruppoNum, String ruoloNome, String ruoloCodice) {}