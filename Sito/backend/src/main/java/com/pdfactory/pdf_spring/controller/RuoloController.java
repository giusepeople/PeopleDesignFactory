package com.pdfactory.pdf_spring.controller;

import com.pdfactory.pdf_spring.dto.RuoloSummaryDTO;
import com.pdfactory.pdf_spring.repository.RuoloRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ruoli")
public class RuoloController {

    private final RuoloRepository ruoloRepository;

    public RuoloController(RuoloRepository ruoloRepository) {
        this.ruoloRepository = ruoloRepository;
    }

    @GetMapping
    public ResponseEntity<List<RuoloSummaryDTO>> getRuoli() {
        List<RuoloSummaryDTO> dto = ruoloRepository.findAll().stream()
                .map(r -> new RuoloSummaryDTO(
                        r.getId(), r.getCodice(), r.getNome(),
                        r.getMissione(), r.getCompetenze(), r.getSuperpoteri(), r.getPuntiCritici()
                ))
                .toList();
        return ResponseEntity.ok(dto);
    }
}