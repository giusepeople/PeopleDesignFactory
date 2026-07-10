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

        creaFase(1, "Briefing", TipoFase.BRIEFING, 10);
        Fase f2 = creaFase(2, "Livello 1 - Diagnosi tecnica", TipoFase.LIVELLO, 15);
        creaFase(3, "Turbativa 1 - Scambio PM", TipoFase.TURBATIVA, 4);
        Fase f4 = creaFase(4, "Livello 2 - Decisione gestionale", TipoFase.LIVELLO, 20);
        creaFase(5, "Livello 3 - Presentazione al cliente", TipoFase.PRESENTAZIONE, 8);
        Fase f6 = creaFase(6, "Livello 4 - Ottimizzazione finale", TipoFase.LIVELLO, 20);
        creaFase(7, "Debrief", TipoFase.DEBRIEF, 25);

        creaModuloLivello1(f2);
        creaModuloLivello2(f4);
        creaModuloLivello4(f6);
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