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
        String restrictedRoleNome,
        String assegnataARuoloNome,     // nome ruolo visibile a tutti (anche se non ristretta -> "Project Manager")
        boolean richiedeGiustificazione // true per le scelte multiple valutate (es. D1)
) {}