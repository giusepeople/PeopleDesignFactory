package com.pdfactory.pdf_spring.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdfactory.pdf_spring.dto.*;
import com.pdfactory.pdf_spring.enums.StatoGioco;
import com.pdfactory.pdf_spring.enums.StatoInvio;
import com.pdfactory.pdf_spring.enums.StatoTeam;
import com.pdfactory.pdf_spring.enums.TipoDomanda;
import com.pdfactory.pdf_spring.enums.TipoFase;
import com.pdfactory.pdf_spring.model.*;
import com.pdfactory.pdf_spring.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/games")
public class ModuloController {

    private final PartitaRepository partitaRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final GruppoRepository gruppoRepository;
    private final ModuloRepository moduloRepository;
    private final DomandaRepository domandaRepository;
    private final InvioModuloRepository invioModuloRepository;
    private final RispostaRepository rispostaRepository;
    private final GameMasterRepository gameMasterRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MINUTI_EXTRA_AUTOMATICI_HINT = 5;

    public ModuloController(PartitaRepository partitaRepository, GiocatoreRepository giocatoreRepository,
                            GruppoRepository gruppoRepository, ModuloRepository moduloRepository,
                            DomandaRepository domandaRepository, InvioModuloRepository invioModuloRepository,
                            RispostaRepository rispostaRepository, GameMasterRepository gameMasterRepository) {
        this.partitaRepository = partitaRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.gruppoRepository = gruppoRepository;
        this.moduloRepository = moduloRepository;
        this.domandaRepository = domandaRepository;
        this.invioModuloRepository = invioModuloRepository;
        this.rispostaRepository = rispostaRepository;
        this.gameMasterRepository = gameMasterRepository;
    }

    // ---------- Giocatore: visualizzazione e compilazione del modulo della fase corrente ----------

    @GetMapping("/{id}/modulo-corrente")
    public ResponseEntity<?> getModuloCorrente(
            @PathVariable UUID id,
            @RequestParam UUID giocatoreId,
            @RequestParam String sessionToken
    ) {
        Partita partita = partitaRepository.findById(id).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }

        Giocatore giocatore = autenticaGiocatore(partita, giocatoreId, sessionToken);
        if (giocatore == null) {
            return ResponseEntity.status(403).body("Sessione non valida");
        }

        Fase fase = partita.getFaseAttuale();
        if (fase == null || fase.getTipo() != TipoFase.LIVELLO) {
            return ResponseEntity.status(404).body("Nessun livello attivo in questo momento");
        }

        Modulo modulo = moduloRepository.findByFaseId(fase.getId()).orElse(null);
        if (modulo == null) {
            return ResponseEntity.status(404).body("Nessun modulo previsto per questa fase");
        }

        Gruppo gruppo = giocatore.getGruppo();
        if (gruppo == null) {
            return ResponseEntity.status(400).body("Non fai ancora parte di un gruppo");
        }

        InvioModulo invio = invioModuloRepository.findByGruppoIdAndModuloId(gruppo.getId(), modulo.getId()).orElse(null);

        return ResponseEntity.ok(buildModuloCorrenteResponse(partita, fase, modulo, giocatore, invio));
    }

    @PostMapping("/{id}/modulo-corrente/salva")
    public ResponseEntity<?> salvaRisposte(@PathVariable UUID id, @RequestBody SalvaRisposteRequest request) {
        return applicaRisposte(id, request, false);
    }

    @PostMapping("/{id}/modulo-corrente/invia")
    public ResponseEntity<?> inviaModulo(@PathVariable UUID id, @RequestBody SalvaRisposteRequest request) {
        return applicaRisposte(id, request, true);
    }

    private ResponseEntity<?> applicaRisposte(UUID partitaId, SalvaRisposteRequest request, boolean finalizza) {
        Partita partita = partitaRepository.findById(partitaId).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }

        if (partita.getStatus() != StatoGioco.IN_CORSO) {
            return ResponseEntity.status(409).body("La partita non è in corso");
        }

        Giocatore giocatore = request == null ? null
                : autenticaGiocatore(partita, request.giocatoreId(), request.sessionToken());
        if (giocatore == null) {
            return ResponseEntity.status(403).body("Sessione non valida");
        }

        Fase fase = partita.getFaseAttuale();
        if (fase == null || fase.getTipo() != TipoFase.LIVELLO) {
            return ResponseEntity.status(404).body("Nessun livello attivo in questo momento");
        }

        Modulo modulo = moduloRepository.findByFaseId(fase.getId()).orElse(null);
        if (modulo == null) {
            return ResponseEntity.status(404).body("Nessun modulo previsto per questa fase");
        }

        Gruppo gruppo = giocatore.getGruppo();
        if (gruppo == null) {
            return ResponseEntity.status(400).body("Non fai ancora parte di un gruppo");
        }

        if (finalizza && (giocatore.getRuolo() == null || !"PM".equals(giocatore.getRuolo().getCodice()))) {
            return ResponseEntity.status(403).body("Solo il Project Manager può inviare il modulo al Game Master");
        }

        InvioModulo invio = invioModuloRepository.findByGruppoIdAndModuloId(gruppo.getId(), modulo.getId()).orElse(null);
        if (invio == null) {
            invio = new InvioModulo();
            invio.setGruppo(gruppo);
            invio.setModulo(modulo);
            invio.setStato(StatoInvio.BOZZA);
        }

        if (invio.getStato() == StatoInvio.APPROVATO) {
            return ResponseEntity.status(409).body("Il modulo è già stato approvato dal Game Master");
        }
        if (invio.getStato() == StatoInvio.INVIATO) {
            return ResponseEntity.status(409).body("Il modulo è in attesa di revisione da parte del Game Master");
        }

        List<RispostaInputDTO> risposteInput = request.risposte() != null ? request.risposte() : List.of();

        for (RispostaInputDTO input : risposteInput) {
            Domanda domanda = domandaRepository.findById(input.domandaId()).orElse(null);
            if (domanda == null || !domanda.getModuloTemplate().getId().equals(modulo.getId())) {
                return ResponseEntity.status(400).body("Domanda non valida");
            }
            if (!puoModificareDomanda(giocatore, domanda)) {
                return ResponseEntity.status(403).body("Non sei autorizzato a compilare la domanda " + domanda.getOrderIndex());
            }
        }

        invio = invioModuloRepository.save(invio);

        for (RispostaInputDTO input : risposteInput) {
            Domanda domanda = domandaRepository.findById(input.domandaId()).orElseThrow();
            InvioModulo invioFinal = invio;
            Risposta risposta = rispostaRepository.findByInvioModuloIdAndDomandaId(invio.getId(), domanda.getId())
                    .orElseGet(() -> {
                        Risposta r = new Risposta();
                        r.setInvioModulo(invioFinal);
                        r.setDomanda(domanda);
                        return r;
                    });

            risposta.setTestoRisposta(input.testoRisposta());
            risposta.setGiustificazione(input.giustificazione());
            risposta.setRispostoDa(giocatore);

            if (domanda.getType() == TipoDomanda.SCELTA_MULTIPLA && domanda.getOpzioneCorretta() != null) {
                boolean corretta = domanda.getOpzioneCorretta().equals(input.testoRisposta());
                risposta.setCorretta(corretta);

                // hint + 5 minuti extra automatici alla PRIMA risposta sbagliata a questa domanda
                if (!corretta && !Boolean.TRUE.equals(risposta.getHintUsato())) {
                    risposta.setHintUsato(true);
                    int attuali = invio.getMinutiExtra() != null ? invio.getMinutiExtra() : 0;
                    invio.setMinutiExtra(attuali + MINUTI_EXTRA_AUTOMATICI_HINT);
                }
            }

            rispostaRepository.save(risposta);
        }

        // persiste eventuali minuti extra automatici assegnati sopra
        invioModuloRepository.save(invio);

        if (finalizza) {
            for (Domanda domanda : modulo.getDomande()) {
                Risposta r = rispostaRepository.findByInvioModuloIdAndDomandaId(invio.getId(), domanda.getId()).orElse(null);
                if (r == null || r.getTestoRisposta() == null || r.getTestoRisposta().isBlank()) {
                    return ResponseEntity.status(400)
                            .body("Rispondete a tutte le domande prima di inviare il modulo (manca la domanda " + domanda.getOrderIndex() + ")");
                }
            }

            invio.setStato(StatoInvio.INVIATO);
            invio.setInviatoDa(giocatore);
            invio.setInviatoIl(Instant.now());
            invio.setMotivoRifiuto(null);
            invioModuloRepository.save(invio);

            gruppo.setStato(StatoTeam.INVIATO);
            gruppoRepository.save(gruppo);
        }

        return ResponseEntity.ok().build();
    }

    // ---------- Game Master: revisione dei moduli inviati dai gruppi ----------

    @GetMapping("/{id}/moduli/revisione")
    public ResponseEntity<?> getRevisione(@PathVariable UUID id, Authentication authentication) {
        Partita partita = partitaRepository.findById(id).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }
        if (!partita.getGameMaster().getNome().equals(authentication.getName())) {
            return ResponseEntity.status(403).body("Non sei il Game Master di questa partita");
        }

        Fase fase = partita.getFaseAttuale();
        if (fase == null || fase.getTipo() != TipoFase.LIVELLO) {
            return ResponseEntity.ok(List.of());
        }

        Modulo modulo = moduloRepository.findByFaseId(fase.getId()).orElse(null);
        if (modulo == null) {
            return ResponseEntity.ok(List.of());
        }

        List<Gruppo> gruppi = gruppoRepository.findByPartitaIdOrderByTeamNumAsc(partita.getId());

        List<InvioModuloGmDTO> response = gruppi.stream()
                .map(gr -> {
                    InvioModulo invio = invioModuloRepository.findByGruppoIdAndModuloId(gr.getId(), modulo.getId()).orElse(null);
                    return buildInvioModuloGmDTO(partita, fase, gr, modulo, invio);
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/moduli/{invioId}/approva")
    public ResponseEntity<?> approvaModulo(@PathVariable UUID id, @PathVariable UUID invioId, Authentication authentication) {
        Partita partita = partitaRepository.findById(id).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }
        if (!partita.getGameMaster().getNome().equals(authentication.getName())) {
            return ResponseEntity.status(403).body("Non sei il Game Master di questa partita");
        }

        InvioModulo invio = invioModuloRepository.findById(invioId).orElse(null);
        if (invio == null || !invio.getGruppo().getPartita().getId().equals(id)) {
            return ResponseEntity.status(404).body("Invio non trovato");
        }

        GameMaster gm = gameMasterRepository.findByNome(authentication.getName()).orElse(null);

        invio.setStato(StatoInvio.APPROVATO);
        invio.setMotivoRifiuto(null);
        invio.setRevisionatoDa(gm);
        invio.setRevisionatoIl(Instant.now());
        invioModuloRepository.save(invio);

        Gruppo gruppo = invio.getGruppo();
        gruppo.setStato(StatoTeam.APPROVATO);
        gruppoRepository.save(gruppo);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/moduli/{invioId}/rifiuta")
    public ResponseEntity<?> rifiutaModulo(@PathVariable UUID id, @PathVariable UUID invioId,
                                           @RequestBody RevisioneModuloRequest request, Authentication authentication) {
        Partita partita = partitaRepository.findById(id).orElse(null);
        if (partita == null) {
            return ResponseEntity.status(404).body("Partita non trovata");
        }
        if (!partita.getGameMaster().getNome().equals(authentication.getName())) {
            return ResponseEntity.status(403).body("Non sei il Game Master di questa partita");
        }
        if (request == null || request.motivoRifiuto() == null || request.motivoRifiuto().isBlank()) {
            return ResponseEntity.status(400).body("Il motivo del rifiuto è obbligatorio");
        }

        InvioModulo invio = invioModuloRepository.findById(invioId).orElse(null);
        if (invio == null || !invio.getGruppo().getPartita().getId().equals(id)) {
            return ResponseEntity.status(404).body("Invio non trovato");
        }

        GameMaster gm = gameMasterRepository.findByNome(authentication.getName()).orElse(null);

        int minutiAggiuntivi = request.minutiExtra() != null ? Math.max(0, request.minutiExtra()) : 0;
        int minutiAccumulati = (invio.getMinutiExtra() != null ? invio.getMinutiExtra() : 0) + minutiAggiuntivi;

        invio.setStato(StatoInvio.RIFIUTATO);
        invio.setMotivoRifiuto(request.motivoRifiuto());
        invio.setMinutiExtra(minutiAccumulati);
        invio.setRevisionatoDa(gm);
        invio.setRevisionatoIl(Instant.now());
        invioModuloRepository.save(invio);

        Gruppo gruppo = invio.getGruppo();
        gruppo.setStato(StatoTeam.RIFIUTATO);
        gruppoRepository.save(gruppo);

        return ResponseEntity.ok().build();
    }

    // ---------- helpers ----------

    private Giocatore autenticaGiocatore(Partita partita, UUID giocatoreId, String sessionToken) {
        if (giocatoreId == null || sessionToken == null) return null;
        Giocatore g = giocatoreRepository.findById(giocatoreId).orElse(null);
        if (g == null || !g.getPartita().getId().equals(partita.getId()) || !g.getSessionToken().equals(sessionToken)) {
            return null;
        }
        return g;
    }

    private boolean puoModificareDomanda(Giocatore giocatore, Domanda domanda) {
        if (domanda.getRestrictedRole() != null) {
            return giocatore.getRuolo() != null && giocatore.getRuolo().getId().equals(domanda.getRestrictedRole().getId());
        }
        return giocatore.getRuolo() != null && "PM".equals(giocatore.getRuolo().getCodice());
    }

    private ModuloCorrenteResponse buildModuloCorrenteResponse(Partita partita, Fase fase, Modulo modulo,
                                                               Giocatore giocatore, InvioModulo invio) {
        List<DomandaModuloDTO> domande = modulo.getDomande().stream().map(this::mapDomanda).toList();

        List<RispostaEsistenteDTO> risposteAttuali = invio == null
                ? List.of()
                : invio.getRisposte().stream()
                .map(r -> {
                    boolean autorizzato = puoModificareDomanda(giocatore, r.getDomanda());
                    boolean presente = r.getTestoRisposta() != null && !r.getTestoRisposta().isBlank();

                    if (autorizzato) {
                        String hint = Boolean.FALSE.equals(r.getCorretta()) ? r.getDomanda().getHintText() : null;
                        return new RispostaEsistenteDTO(
                                r.getDomanda().getId(), r.getTestoRisposta(), r.getGiustificazione(),
                                presente, r.getCorretta(), hint
                        );
                    }

                    // non autorizzato: nessun contenuto, solo il flag di presenza
                    return new RispostaEsistenteDTO(r.getDomanda().getId(), null, null, presente, null, null);
                })
                .toList();

        String invioStato = invio == null ? "BOZZA" : invio.getStato().name();
        String motivoRifiuto = invio != null ? invio.getMotivoRifiuto() : null;
        Integer minutiExtra = invio != null && invio.getMinutiExtra() != null ? invio.getMinutiExtra() : 0;

        boolean sonoIoPM = giocatore.getRuolo() != null && "PM".equals(giocatore.getRuolo().getCodice());
        String mioRuoloCodice = giocatore.getRuolo() != null ? giocatore.getRuolo().getCodice() : null;

        Long secondiRimanenti = null;
        Instant adesso = Instant.now();
        if (partita.getFaseIniziataIl() != null) {
            long durataSec = (fase.getDefaultDurataMinuti() + minutiExtra) * 60L;
            long trascorsi = adesso.getEpochSecond() - partita.getFaseIniziataIl().getEpochSecond();
            secondiRimanenti = Math.max(0, durataSec - trascorsi);
        }

        return new ModuloCorrenteResponse(
                modulo.getId(), modulo.getTitolo(), fase.getContenutoTesto(), parseDati(fase.getDatiJson()),
                domande, risposteAttuali,
                invioStato, motivoRifiuto, minutiExtra, sonoIoPM, mioRuoloCodice,
                secondiRimanenti, adesso
        );
    }

    private InvioModuloGmDTO buildInvioModuloGmDTO(Partita partita, Fase fase, Gruppo gruppo, Modulo modulo, InvioModulo invio) {
        List<RispostaGmDTO> risposte = modulo.getDomande().stream()
                .map(d -> {
                    Risposta r = invio == null ? null
                            : rispostaRepository.findByInvioModuloIdAndDomandaId(invio.getId(), d.getId()).orElse(null);
                    return new RispostaGmDTO(
                            d.getId(), d.getOrderIndex(), d.getText(), d.getType().name(),
                            r != null ? r.getTestoRisposta() : null,
                            r != null ? r.getGiustificazione() : null,
                            r != null ? r.getCorretta() : null,
                            d.getOpzioneCorretta(),
                            d.getHintText(),
                            r != null && r.getRispostoDa() != null ? r.getRispostoDa().getNickname() : null
                    );
                })
                .toList();

        Integer minutiExtra = invio != null && invio.getMinutiExtra() != null ? invio.getMinutiExtra() : 0;

        Long secondiRimanenti = null;
        if (partita.getFaseIniziataIl() != null) {
            long durataSec = (fase.getDefaultDurataMinuti() + minutiExtra) * 60L;
            long trascorsi = Instant.now().getEpochSecond() - partita.getFaseIniziataIl().getEpochSecond();
            secondiRimanenti = Math.max(0, durataSec - trascorsi);
        }

        return new InvioModuloGmDTO(
                invio != null ? invio.getId() : null,
                gruppo.getId(),
                gruppo.getTeamNum(),
                gruppo.getStato().name(),
                invio != null ? invio.getStato().name() : "BOZZA",
                invio != null ? invio.getInviatoIl() : null,
                invio != null ? invio.getMotivoRifiuto() : null,
                minutiExtra,
                secondiRimanenti,
                risposte
        );
    }

    private DomandaModuloDTO mapDomanda(Domanda d) {
        String assegnataA = d.getRestrictedRole() != null ? d.getRestrictedRole().getNome() : "Project Manager";
        boolean richiedeGiustificazione = d.getType() == TipoDomanda.SCELTA_MULTIPLA && d.getOpzioneCorretta() != null;

        return new DomandaModuloDTO(
                d.getId(), d.getOrderIndex(), d.getType().name(), d.getText(),
                parseOpzioni(d.getOpzioneJson()),
                d.getRestrictedRole() != null ? d.getRestrictedRole().getCodice() : null,
                d.getRestrictedRole() != null ? d.getRestrictedRole().getNome() : null,
                assegnataA,
                richiedeGiustificazione
        );
    }

    private List<OpzioneDTO> parseOpzioni(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<List<OpzioneDTO>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    private List<DatoBriefingDTO> parseDati(String datiJson) {
        if (datiJson == null || datiJson.isBlank()) return List.of();
        try {
            return objectMapper.readValue(datiJson, new TypeReference<List<DatoBriefingDTO>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}