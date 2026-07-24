package com.pdfactory.pdf_spring.dto;

public record OpzioneLivelloDTO(
        String valore,
        String titolo,
        String descrizione,
        String costoStimato,
        String tempo,
        String rischio
) {}