package com.pdfactory.pdf_spring.dto;

import java.util.List;
import java.util.UUID;

public record DomandaModuloDTO(
        UUID id,
        Integer orderIndex,
        String type,
        String text,
        List<OpzioneDTO> opzioni,
        String restrictedRoleCodice,
        String restrictedRoleNome
) {}