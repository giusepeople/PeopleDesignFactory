package com.pdfactory.pdf_spring.controller;

import com.pdfactory.pdf_spring.dto.*;
import com.pdfactory.pdf_spring.enums.StatoGioco;
import com.pdfactory.pdf_spring.model.*;
import com.pdfactory.pdf_spring.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {

    // niente 0/O/1/I: caratteri troppo simili da leggere a schermo o dettare a voce
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DIMENSIONE_GRUPPO = 5;

    private final PartitaRepository partitaRepository;
    private final GameMasterRepository gameMasterRepository;
    private final FaseRepository faseRepository;
    private final ModuloRepository moduloRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final GruppoRepository gruppoRepository;
    private final RuoloRepository ruoloRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameController(PartitaRepository partitaRepository, GameMasterRepository gameMasterRepository,
                          FaseRepository faseRepository, ModuloRepository moduloRepository,
                          GiocatoreRepository giocatoreRepository, GruppoRepository gruppoRepository,
                          RuoloRepository ruoloRepository) {
        this.partitaRepository = partitaRepository;
        this.gameMasterRepository = gameMasterRepository;
        this.faseRepository = faseRepository;
        this.moduloRepository = moduloRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.gruppoRepository = gruppoRepository;
        this.ruoloRepository = ruoloRepository;
    }

    // ---------- GM: creazione e lista partite ----------

    @PostMapping
    public ResponseEntity<CreateGameResponse> createGame(Authentication authentication) {
        GameMaster gm = gameMasterRepository.findByNome(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("GM non trovato"));

        Fase primaFase = faseRepository.findAllByOrderByOrdinalAsc().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Struttura di gioco non inizializzata"));

        Partita partita = new Partita();
        partita.setGameMaster(gm);
        partita.setCodPartita(generateUniqueCode());
        partita.setStatus(StatoGioco.IN_ATTESA);
        partita.setFaseAttuale(primaFase);

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

    @GetMapping("/struttura")
    public ResponseEntity<List<FaseSummary>> getStruttura() {
        List<FaseSummary> response = faseRepository.findAllByOrderByOrdinalAsc().stream()
                .map(fase -> {
                    ModuloSummary moduloSummary = moduloRepository.findByFaseId(fase.getId())
                            .map(modulo -> new ModuloSummary(
                                    modulo.getId(),
                                    modulo.getTitolo(),
                                    modulo.getDomande().stream()
                                            .map(d -> new DomandaSummary(d.getId(), d.getType().name(), d.getText(),
                                                    d.getRestrictedRole() != null))
                                            .toList()
                            ))
                            .orElse(null);

                    return new FaseSummary(fase.getId(), fase.getOrdinal(), fase.getNome(),
                            fase.getTipo().name(), fase.getDefaultDurataMinuti(), moduloSummary);
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    // ---------- Giocatore: ingresso in lobby ----------

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinGameRequest request) {
        if (request.codice() == null || request.codice().isBlank()) {
            return ResponseEntity.badRequest().body("Codice partita mancante");
        }

        String nickname = request.nickname() == null ? "" : request.nickname().trim();
        if (nickname.isBlank()) {
            return ResponseEntity.badRequest().body("Il nickname è obbligatorio");
        }

        Partita partita = partitaRepository.findByCodPartita(request.codice().trim().toUpperCase())
                .orElse(null);

        if (partita == null) {
            return ResponseEntity.status(404).body("Codice partita non valido");
        }

        if (partita.getStatus() != StatoGioco.IN_ATTESA) {
            return ResponseEntity.status(409).body("La partita è già stata avviata o è terminata");
        }

        boolean nicknameInUso = giocatoreRepository
                .findByPartitaIdAndNicknameIgnoreCase(partita.getId(), nickname)
                .isPresent();

        if (nicknameInUso) {
            return ResponseEntity.status(409).body("Nickname già in uso in questa partita");
        }

        Giocatore giocatore = new Giocatore();
        giocatore.setPartita(partita);
        giocatore.setNickname(nickname);
        giocatore.setSessionToken(UUID.randomUUID().toString());
        giocatoreRepository.save(giocatore);

        return ResponseEntity.ok(new JoinGameResponse(
                giocatore.getId(), partita.getId(), giocatore.getSessionToken(), giocatore.getNickname()
        ));
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<?> getState(@PathVariable UUID id) {
        Partita partita = partitaRepository.findById(id).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }

        List<Giocatore> giocatori = giocatoreRepository.findByPartitaId(id);

        List<GiocatoreLobbyDTO> dto = giocatori.stream()
                .map(g -> new GiocatoreLobbyDTO(
                        g.getId(),
                        g.getNickname(),
                        g.getGruppo() != null ? g.getGruppo().getTeamNum() : null,
                        g.getRuolo() != null ? g.getRuolo().getNome() : null,
                        g.getRuolo() != null ? g.getRuolo().getCodice() : null
                ))
                .toList();

        return ResponseEntity.ok(new LobbyStateResponse(
                partita.getId(), partita.getCodPartita(), partita.getStatus().name(), giocatori.size(), dto
        ));
    }

    // ---------- GM: avvio partita e formazione gruppi ----------

    @PostMapping("/{id}/avvia")
    public ResponseEntity<?> avviaPartita(@PathVariable UUID id, Authentication authentication) {
        Partita partita = partitaRepository.findById(id).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }

        if (!partita.getGameMaster().getNome().equals(authentication.getName())) {
            return ResponseEntity.status(403).body("Non sei il Game Master di questa partita");
        }

        if (partita.getStatus() != StatoGioco.IN_ATTESA) {
            return ResponseEntity.status(409).body("La partita è già stata avviata");
        }

        List<Giocatore> giocatori = giocatoreRepository.findByPartitaId(id);
        if (giocatori.size() < DIMENSIONE_GRUPPO) {
            return ResponseEntity.status(400).body("Servono almeno " + DIMENSIONE_GRUPPO + " giocatori per avviare");
        }

        assegnaGruppiERuoli(partita, giocatori);

        partita.setStatus(StatoGioco.IN_CORSO);
        partita.setStartedAt(Instant.now());
        partita.setFaseIniziataIl(Instant.now());
        partitaRepository.save(partita);

        return ResponseEntity.ok(buildPannello(partita));
    }

    @GetMapping("/{id}/pannello")
    public ResponseEntity<?> getPannello(@PathVariable UUID id, Authentication authentication) {
        Partita partita = partitaRepository.findById(id).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }

        if (!partita.getGameMaster().getNome().equals(authentication.getName())) {
            return ResponseEntity.status(403).body("Non sei il Game Master di questa partita");
        }

        return ResponseEntity.ok(buildPannello(partita));
    }

    // ---------- helpers ----------

    private void assegnaGruppiERuoli(Partita partita, List<Giocatore> giocatori) {
        List<Giocatore> pool = new ArrayList<>(giocatori);
        Collections.shuffle(pool, RANDOM);

        List<Ruolo> ruoliBase = ruoloRepository.findAll();
        if (ruoliBase.size() < DIMENSIONE_GRUPPO) {
            throw new IllegalStateException("I ruoli di gioco non sono stati inizializzati correttamente");
        }

        int teamNum = 1;
        int index = 0;

        while (index < pool.size()) {
            int fine = Math.min(index + DIMENSIONE_GRUPPO, pool.size());
            List<Giocatore> membriGruppo = pool.subList(index, fine);

            Gruppo gruppo = new Gruppo();
            gruppo.setPartita(partita);
            gruppo.setTeamNum(teamNum);
            gruppo = gruppoRepository.save(gruppo);

            List<Ruolo> ruoliMescolati = new ArrayList<>(ruoliBase);
            Collections.shuffle(ruoliMescolati, RANDOM);

            for (int i = 0; i < membriGruppo.size(); i++) {
                Giocatore g = membriGruppo.get(i);
                g.setGruppo(gruppo);
                g.setRuolo(ruoliMescolati.get(i % ruoliMescolati.size()));
                giocatoreRepository.save(g);
            }

            teamNum++;
            index = fine;
        }
    }

    private PannelloControlloResponse buildPannello(Partita partita) {
        List<Giocatore> tutti = giocatoreRepository.findByPartitaId(partita.getId());
        List<Gruppo> gruppi = gruppoRepository.findByPartitaIdOrderByTeamNumAsc(partita.getId());

        List<GruppoDettaglioDTO> gruppiDto = gruppi.stream()
                .map(gr -> {
                    List<GiocatoreDettaglioDTO> membri = tutti.stream()
                            .filter(g -> g.getGruppo() != null && g.getGruppo().getId().equals(gr.getId()))
                            .map(g -> new GiocatoreDettaglioDTO(
                                    g.getId(), g.getNickname(),
                                    g.getRuolo() != null ? g.getRuolo().getNome() : null,
                                    g.getRuolo() != null ? g.getRuolo().getCodice() : null
                            ))
                            .toList();
                    return new GruppoDettaglioDTO(gr.getId(), gr.getTeamNum(), gr.getStato().name(), membri);
                })
                .toList();

        List<GiocatoreDettaglioDTO> senzaGruppo = tutti.stream()
                .filter(g -> g.getGruppo() == null)
                .map(g -> new GiocatoreDettaglioDTO(g.getId(), g.getNickname(), null, null))
                .toList();

        FaseCorrenteDTO faseDto = null;
        if (partita.getFaseAttuale() != null) {
            Fase f = partita.getFaseAttuale();
            faseDto = new FaseCorrenteDTO(f.getId(), f.getOrdinal(), f.getNome(), f.getTipo().name(), f.getDefaultDurataMinuti());
        }

        return new PannelloControlloResponse(
                partita.getId(), partita.getCodPartita(), partita.getStatus().name(),
                tutti.size(), faseDto, partita.getFaseIniziataIl(), gruppiDto, senzaGruppo
        );
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

    // ---------- fase corrente (pubblico: GM e giocatori) ----------

    @GetMapping("/{id}/fase-corrente")
    public ResponseEntity<?> getFaseCorrente(@PathVariable UUID id) {
        Partita partita = partitaRepository.findById(id).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }

        return ResponseEntity.ok(buildFaseCorrenteResponse(partita));
    }

    // ---------- GM: avanzamento manuale di fase ----------

    @PostMapping("/{id}/avanza-fase")
    public ResponseEntity<?> avanzaFase(@PathVariable UUID id, Authentication authentication) {
        Partita partita = partitaRepository.findById(id).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }

        if (!partita.getGameMaster().getNome().equals(authentication.getName())) {
            return ResponseEntity.status(403).body("Non sei il Game Master di questa partita");
        }

        if (partita.getStatus() != StatoGioco.IN_CORSO) {
            return ResponseEntity.status(409).body("La partita non è in corso");
        }

        List<Fase> fasi = faseRepository.findAllByOrderByOrdinalAsc();
        Fase attuale = partita.getFaseAttuale();

        Fase prossima = fasi.stream()
                .filter(f -> attuale == null || f.getOrdinal() > attuale.getOrdinal())
                .findFirst()
                .orElse(null);

        if (prossima == null) {
            partita.setStatus(StatoGioco.TERMINATA);
            partita.setEndedAt(Instant.now());
        } else {
            partita.setFaseAttuale(prossima);
            partita.setFaseIniziataIl(Instant.now());
        }

        partitaRepository.save(partita);

        return ResponseEntity.ok(buildPannello(partita));
    }

    private FaseCorrenteResponse buildFaseCorrenteResponse(Partita partita) {
        Fase fase = partita.getFaseAttuale();

        FaseCorrenteDTO faseDto = null;
        Long secondiRimanenti = null;
        String contenuto = null;
        List<DatoBriefingDTO> dati = List.of();

        Instant adesso = Instant.now();

        if (fase != null) {
            faseDto = new FaseCorrenteDTO(fase.getId(), fase.getOrdinal(), fase.getNome(),
                    fase.getTipo().name(), fase.getDefaultDurataMinuti());
            contenuto = fase.getContenutoTesto();
            dati = parseDati(fase.getDatiJson());

            if (partita.getFaseIniziataIl() != null) {
                long durataSec = fase.getDefaultDurataMinuti() * 60L;
                long trascorsi = adesso.getEpochSecond() - partita.getFaseIniziataIl().getEpochSecond();
                secondiRimanenti = Math.max(0, durataSec - trascorsi);
            }
        }

        return new FaseCorrenteResponse(
                partita.getStatus().name(), faseDto, partita.getFaseIniziataIl(), secondiRimanenti, contenuto, dati,
                adesso
        );
    }

    private List<DatoBriefingDTO> parseDati(String datiJson) {
        if (datiJson == null || datiJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(datiJson, new TypeReference<List<DatoBriefingDTO>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}