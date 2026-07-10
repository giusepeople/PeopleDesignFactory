package com.pdfactory.pdf_spring.controller;

import com.pdfactory.pdf_spring.dto.CreateGameResponse;
import com.pdfactory.pdf_spring.enums.StatoGioco;
import com.pdfactory.pdf_spring.model.GameMaster;
import com.pdfactory.pdf_spring.model.Partita;
import com.pdfactory.pdf_spring.repository.GameMasterRepository;
import com.pdfactory.pdf_spring.repository.PartitaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    // niente 0/O/1/I: caratteri troppo simili da leggere a schermo o dettare a voce
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PartitaRepository partitaRepository;
    private final GameMasterRepository gameMasterRepository;

    public GameController(PartitaRepository partitaRepository, GameMasterRepository gameMasterRepository) {
        this.partitaRepository = partitaRepository;
        this.gameMasterRepository = gameMasterRepository;
    }

    @PostMapping
    public ResponseEntity<CreateGameResponse> createGame(Authentication authentication) {
        GameMaster gm = gameMasterRepository.findByNome(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("GM non trovato"));

        Partita partita = new Partita();
        partita.setGameMaster(gm);
        partita.setCodPartita(generateUniqueCode());
        partita.setStatus(StatoGioco.IN_ATTESA);

        partitaRepository.save(partita);

        return ResponseEntity.ok(
                new CreateGameResponse(partita.getId(), partita.getCodPartita(), partita.getStatus().name())
        );
    }

    @GetMapping
    public ResponseEntity<List<CreateGameResponse>> getMyGames(Authentication authentication) {
        List<Partita> partite = partitaRepository
                .findByGameMaster_NomeOrderByCreatedAtDesc(authentication.getName());

        List<CreateGameResponse> response = partite.stream()
                .map(p -> new CreateGameResponse(p.getId(), p.getCodPartita(), p.getStatus().name()))
                .toList();

        return ResponseEntity.ok(response);
    }

    private String generateUniqueCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (partitaRepository.findByCodPartita(code).isPresent());

        return code;
    }
}