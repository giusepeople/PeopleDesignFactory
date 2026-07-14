package com.pdfactory.pdf_spring.config;

import com.pdfactory.pdf_spring.enums.TipoDomanda;
import com.pdfactory.pdf_spring.enums.TipoFase;
import com.pdfactory.pdf_spring.model.Domanda;
import com.pdfactory.pdf_spring.model.Fase;
import com.pdfactory.pdf_spring.model.Modulo;
import com.pdfactory.pdf_spring.repository.FaseRepository;
import com.pdfactory.pdf_spring.repository.ModuloRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2) // dopo il DataSeeder del GM
public class GameStructureSeeder implements CommandLineRunner {

    private final FaseRepository faseRepository;
    private final ModuloRepository moduloRepository;

    public GameStructureSeeder(FaseRepository faseRepository, ModuloRepository moduloRepository) {
        this.faseRepository = faseRepository;
        this.moduloRepository = moduloRepository;
    }

    @Override
    public void run(String... args) {
        if (!faseRepository.findAllByOrderByOrdinalAsc().isEmpty()) {
            return; // già seedato, non duplicare
        }

        Fase f1 = creaFase(1, "Briefing", TipoFase.BRIEFING, 10);
        Fase f2 = creaFase(2, "Livello 1 - Diagnosi tecnica", TipoFase.LIVELLO, 15);
        creaFase(3, "Turbativa 1 - Scambio PM", TipoFase.TURBATIVA, 4);
        Fase f4 = creaFase(4, "Livello 2 - Decisione gestionale", TipoFase.LIVELLO, 20);
        creaFase(5, "Livello 3 - Presentazione al cliente", TipoFase.PRESENTAZIONE, 8);
        Fase f6 = creaFase(6, "Livello 4 - Ottimizzazione finale", TipoFase.LIVELLO, 20);
        creaFase(7, "Debrief", TipoFase.DEBRIEF, 25);

        popolaBriefing(f1);

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
        modulo.setTitolo("Diagnosi tecnica - Modulo gruppo");

        aggiungiDomanda(modulo, 1, TipoDomanda.SCELTA_MULTIPLA,
                "Sulla base delle osservazioni visive, qual è la causa più probabile del difetto?",
                "Guarda la zona della frattura rispetto alla direzione del carico.");
        aggiungiDomanda(modulo, 2, TipoDomanda.APERTA,
                "Quali ulteriori dati raccogliereste per confermare l'ipotesi? (max 3 righe)", null);
        aggiungiDomanda(modulo, 3, TipoDomanda.SCELTA_MULTIPLA,
                "In base ai dati di processo forniti, quale parametro risulta fuori tolleranza?",
                "Confronta i valori con la scheda tecnica del materiale.");
        aggiungiDomanda(modulo, 4, TipoDomanda.APERTA,
                "Il componente rispetta il margine di sicurezza richiesto dal calcolo rapido? Motivate.", null);

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
        Domanda domanda = new Domanda();
        domanda.setModuloTemplate(modulo);
        domanda.setOrderIndex(ordine);
        domanda.setType(tipo);
        domanda.setText(testo);
        domanda.setHintText(hint);
        modulo.getDomande().add(domanda);
    }
}