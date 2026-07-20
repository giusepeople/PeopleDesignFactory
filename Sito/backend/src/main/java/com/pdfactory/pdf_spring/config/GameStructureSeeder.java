package com.pdfactory.pdf_spring.config;

import com.pdfactory.pdf_spring.enums.TipoDomanda;
import com.pdfactory.pdf_spring.enums.TipoFase;
import com.pdfactory.pdf_spring.model.Domanda;
import com.pdfactory.pdf_spring.model.Fase;
import com.pdfactory.pdf_spring.model.Modulo;
import com.pdfactory.pdf_spring.model.Ruolo;
import com.pdfactory.pdf_spring.repository.FaseRepository;
import com.pdfactory.pdf_spring.repository.ModuloRepository;
import com.pdfactory.pdf_spring.repository.RuoloRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2) // dopo il DataSeeder/RuoloSeeder del GM e dei ruoli
public class GameStructureSeeder implements CommandLineRunner {

    private final FaseRepository faseRepository;
    private final ModuloRepository moduloRepository;
    private final RuoloRepository ruoloRepository;

    public GameStructureSeeder(FaseRepository faseRepository, ModuloRepository moduloRepository,
                               RuoloRepository ruoloRepository) {
        this.faseRepository = faseRepository;
        this.moduloRepository = moduloRepository;
        this.ruoloRepository = ruoloRepository;
    }

    @Override
    public void run(String... args) {
        if (!faseRepository.findAllByOrderByOrdinalAsc().isEmpty()) {
            return; // già seedato, non duplicare
        }

        Fase f1 = creaFase(1, "Briefing", TipoFase.BRIEFING, 10);
        Fase f2 = creaFase(2, "Livello 1 - Diagnosi tecnica", TipoFase.LIVELLO, 20);
        Fase f3 = creaFase(3, "Turbativa 1 - Scambio PM", TipoFase.TURBATIVA, 5);
        Fase f4 = creaFase(4, "Livello 2 - Decisione gestionale", TipoFase.LIVELLO, 20);
        creaFase(5, "Livello 3 - Presentazione al cliente", TipoFase.PRESENTAZIONE, 8);
        Fase f6 = creaFase(6, "Livello 4 - Ottimizzazione finale", TipoFase.LIVELLO, 20);
        creaFase(7, "Debrief", TipoFase.DEBRIEF, 25);

        popolaBriefing(f1);
        popolaDatiLivello1(f2);
        popolaTurbativa1(f3);

        creaModuloLivello1(f2);
        creaModuloLivello2(f4);
        creaModuloLivello4(f6);
    }

    private void popolaBriefing(Fase fase) {
        fase.setContenutoTesto(
                "Siete il team di progettazione di PEOPLE DESIGN Factory, una PMI meccanica italiana che " +
                        "produce sistemi di trasmissione per il settore automotive e machinery. L'azienda ha " +
                        "120 dipendenti, un ufficio tecnico di 15 persone e lavora principalmente per clienti " +
                        "OEM tedeschi e nordici.\n\n" +
                        "Tre giorni fa, il cliente NORDAUTO AG (principale cliente, 35% del fatturato) ha " +
                        "rilevato un problema critico sul prototipo del nuovo albero di trasmissione per SUV " +
                        "elettrici — il progetto più importante degli ultimi 5 anni. Il lancio in produzione è " +
                        "previsto tra 6 settimane.\n\n" +
                        "NORDAUTO ha inviato questa mattina una mail formale: se entro 48 ore non ricevono " +
                        "un'analisi della causa del problema e un piano di azione credibile, metteranno in " +
                        "discussione l'intero contratto (valore: 2,3 milioni di euro/anno).\n\n" +
                        "Il vostro capo, Direttore Tecnico Ing. Moretti, è in aeroporto per un volo d'emergenza " +
                        "verso Monaco. Prima di imbarcarsi, vi ha chiamato e ha detto solo: \"Ragazzi, mi fido " +
                        "di voi. Avete 48 ore. Fate funzionare l'ufficio tecnico come se fossi io ad essere lì.\"\n\n" +
                        "Il gioco inizia adesso. Ogni livello che superate vi avvicina alla soluzione. Ogni " +
                        "livello bloccato vi avvicina alla crisi."
        );

        fase.setDatiJson("""
            [
              {"label": "Componente", "valore": "Albero di Trasmissione AT-7X"},
              {"label": "Applicazione", "valore": "Trasmissione per SUV elettrico premium (segmento D)"},
              {"label": "Materiale", "valore": "Acciaio 42CrMo4 bonificato (presunta, da verificare)"},
              {"label": "Trattamento", "valore": "Tempra ad induzione sulla sede dei cuscinetti"},
              {"label": "Diametro nominale", "valore": "Ø45 mm sul tratto principale, Ø32 mm sui tratti ridotti"},
              {"label": "Coppia max trasmessa", "valore": "650 Nm (picco EV: 900 Nm)"},
              {"label": "Problema riscontrato", "valore": "Cricca superficiale rilevata con liquidi penetranti nella zona di raccordo R3 tra il tratto Ø45 e Ø32"},
              {"label": "Ore di test al rilevamento", "valore": "1.840 h (su 5.000 h richieste dal capitolato)"}
            ]
            """);

        faseRepository.save(fase);
    }

    private void popolaDatiLivello1(Fase fase) {
        fase.setContenutoTesto(
                "Il laboratorio di NORDAUTO ha inviato le seguenti informazioni preliminari sul guasto rilevato. " +
                        "Analizzatele e identificate la causa principale.\n\n" +
                        "OSSERVAZIONI VISIVE\n" +
                        "- Cricca a partenza superficiale nella zona di raccordo (raggio R3) tra tratto Ø45 e Ø32 mm\n" +
                        "- Propagazione della cricca a ~45° rispetto all'asse (classico da torsione o fatica combinata)\n" +
                        "- Nessuna deformazione plastica macroscopica visibile\n" +
                        "- Superficie di frattura con aspetto 'a conchiglia' (beachmarks visibili)\n\n" +
                        "DATI DI PROCESSO (dall'ufficio tecnico di PEOPLE DESIGN FACTORY)\n" +
                        "- Il raggio di raccordo progettuale era R5, ma le ultime due commesse sono state prodotte con R3 per un errore nel file CNC non rilevato in fase di controllo\n" +
                        "- Il trattamento di tempra ad induzione è stato eseguito correttamente (durezza verificata: 58-62 HRC)\n" +
                        "- Il materiale è 42CrMo4 bonificato: Rm = 1000 MPa, Re = 850 MPa (valori nella norma)\n" +
                        "- Il test endurance ha applicato il 100% del carico ciclico da specifica (650 Nm, R=-1, freq. 30 Hz)"
        );

        fase.setDatiJson("""
        [
          {"label": "Kt raccordo R5", "valore": "≈ 1.45"},
          {"label": "Kt raccordo R3", "valore": "≈ 2.10"},
          {"label": "Tensione alternata con Kt R5", "valore": "~310 MPa (sotto il limite a fatica ~400 MPa)"},
          {"label": "Tensione alternata con Kt R3", "valore": "~450 MPa (SOPRA il limite a fatica)"}
        ]
        """);

        faseRepository.save(fase);
    }

    private void popolaTurbativa1(Fase fase) {
        fase.setContenutoTesto(
                "\"ATTENZIONE PEOPLE DESIGN FACTORY. Il Direttore Moretti ha appena chiamato dall'aeroporto. " +
                        "Per esigenze aziendali urgenti, tutti i Project Manager devono spostarsi immediatamente " +
                        "al tavolo successivo (senso orario). Avete 2 minuti per fare il briefing con il vostro " +
                        "nuovo team. Il tempo del Livello 2 inizia subito dopo.\"\n\n" +
                        "Se sei il Project Manager: raggiungi il nuovo gruppo indicato qui sotto e fatti spiegare " +
                        "rapidamente la situazione dal team. Quando l'handover è completo, premi il pulsante " +
                        "\"pronto\".\n\n" +
                        "Se non sei il Project Manager: il tuo gruppo sta per ricevere un nuovo PM. Aiutalo a " +
                        "inserirsi il più rapidamente possibile: dovrà presentare lui il prossimo modulo."
        );
        faseRepository.save(fase);
    }

    private Fase creaFase(int ordinal, String nome, TipoFase tipo, int durata) {
        Fase fase = new Fase();
        fase.setOrdinal(ordinal);
        fase.setNome(nome);
        fase.setTipo(tipo);
        fase.setDefaultDurataMinuti(durata);
        return faseRepository.save(fase);
    }

    private void creaModuloLivello1(Fase fase) {
        Modulo modulo = new Modulo();
        modulo.setFase(fase);
        modulo.setTitolo("Diagnosi tecnica - Foglio Risposta L1");

        Ruolo senior = ruoloRepository.findByCodice("SENIOR").orElse(null);

        String opzioniD1 = """
            [
              {"valore": "A", "etichetta": "Difetto del materiale (inclusioni o disomogeneità del 42CrMo4)"},
              {"valore": "B", "etichetta": "Errore di processo nel trattamento termico (tempra non uniforme)"},
              {"valore": "C", "etichetta": "Errore geometrico nel raccordo (R3 invece di R5) con conseguente sovra-concentrazione delle tensioni a fatica"},
              {"valore": "D", "etichetta": "Sovraccarico accidentale durante i test (carichi applicati oltre specifica)"}
            ]
            """;

        aggiungiDomanda(modulo, 1, TipoDomanda.SCELTA_MULTIPLA,
                "Qual è la causa principale del cedimento dell'albero AT-7X? Scegli tra le opzioni e giustifica in 3-5 righe.",
                "Guarda la zona della frattura rispetto alla direzione del carico.",
                opzioniD1, "C", null);

        aggiungiDomanda(modulo, 2, TipoDomanda.APERTA,
                "Quale dato numerico vi ha permesso di escludere le altre opzioni? Citate almeno un valore dal briefing.",
                null, null, null, null);

        aggiungiDomanda(modulo, 3, TipoDomanda.APERTA,
                "[CAMPO SENIOR] Il Progettista Senior descrive sinteticamente il meccanismo di cedimento a fatica e " +
                        "spiega perché la variazione di raggio da R5 a R3 è critica in questo caso specifico " +
                        "(contesto EV con picchi di coppia elevati).",
                null, null, null, senior);

        aggiungiDomanda(modulo, 4, TipoDomanda.APERTA,
                "Se doveste comunicare la causa al cliente NORDAUTO in 2 frasi (stile executive), come lo fareste? " +
                        "Scrivete le 2 frasi.",
                null, null, null, null);

        moduloRepository.save(modulo);
    }

    private void creaModuloLivello2(Fase fase) {
        Modulo modulo = new Modulo();
        modulo.setFase(fase);
        modulo.setTitolo("Decisione gestionale - Modulo gruppo");

        aggiungiDomanda(modulo, 1, TipoDomanda.SCELTA_MULTIPLA,
                "Quale delle 3 opzioni disponibili scegliete come piano d'azione principale?", null);
        aggiungiDomanda(modulo, 2, TipoDomanda.APERTA,
                "Motivate la scelta in termini di costi/tempi/qualità.", null);
        aggiungiDomanda(modulo, 3, TipoDomanda.APERTA,
                "Come cambia la scelta alla luce della complicazione emersa dopo 8 minuti?", null);
        aggiungiDomanda(modulo, 4, TipoDomanda.APERTA,
                "Quali rischi residui accettate consapevolmente?", null);
        aggiungiDomanda(modulo, 5, TipoDomanda.APERTA,
                "Quali risorse aggiuntive richiedete, se ce ne sono?", null);

        moduloRepository.save(modulo);
    }

    private void creaModuloLivello4(Fase fase) {
        Modulo modulo = new Modulo();
        modulo.setFase(fase);
        modulo.setTitolo("Ottimizzazione finale - Modulo gruppo");

        aggiungiDomanda(modulo, 1, TipoDomanda.APERTA,
                "Quali azioni correttive proponete per evitare che il problema si ripresenti?", null);
        aggiungiDomanda(modulo, 2, TipoDomanda.APERTA,
                "Come integrate la soluzione tecnica con i vincoli gestionali emersi prima?", null);
        aggiungiDomanda(modulo, 3, TipoDomanda.APERTA,
                "Quale piano di verifica/collaudo proponete prima dell'avvio produzione?", null);

        moduloRepository.save(modulo);
    }

    private void aggiungiDomanda(Modulo modulo, int ordine, TipoDomanda tipo, String testo, String hint) {
        aggiungiDomanda(modulo, ordine, tipo, testo, hint, null, null, null);
    }

    private void aggiungiDomanda(Modulo modulo, int ordine, TipoDomanda tipo, String testo, String hint,
                                 String opzioniJson, String opzioneCorretta, Ruolo restrictedRole) {
        Domanda domanda = new Domanda();
        domanda.setModuloTemplate(modulo);
        domanda.setOrderIndex(ordine);
        domanda.setType(tipo);
        domanda.setText(testo);
        domanda.setHintText(hint);
        domanda.setOpzioneJson(opzioniJson);
        domanda.setOpzioneCorretta(opzioneCorretta);
        domanda.setRestrictedRole(restrictedRole);
        modulo.getDomande().add(domanda);
    }
}