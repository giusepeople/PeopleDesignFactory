package com.pdfactory.pdf_spring.config;

import com.pdfactory.pdf_spring.model.Ruolo;
import com.pdfactory.pdf_spring.repository.RuoloRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RuoloSeeder implements CommandLineRunner {

    private final RuoloRepository ruoloRepository;

    public RuoloSeeder(RuoloRepository ruoloRepository) {
        this.ruoloRepository = ruoloRepository;
    }

    @Override
    public void run(String... args) {
        if (!ruoloRepository.findAll().isEmpty()) {
            return; // già seedato
        }

        creaRuolo("PM", "Project Manager",
                "Coordina il gruppo, gestisce i tempi e decide cosa e quando inviare al Game Master. " +
                        "È l'unico abilitato a presentare il piano al cliente.",
                "Visione d'insieme, gestione del tempo, sintesi delle informazioni del gruppo.",
                "È l'unico che può inviare i moduli al Game Master (salvo eccezioni indicate dal modulo stesso).",
                "Non ha accesso diretto ai dettagli tecnici approfonditi: deve fidarsi dei progettisti.");

        creaRuolo("SENIOR", "Progettista Meccanico Senior",
                "Guida l'analisi tecnica del problema e valida le ipotesi sulla causa del guasto.",
                "Lettura di disegni tecnici, analisi dei materiali, calcolo di verifica rapido.",
                "Può richiedere un hint tecnico aggiuntivo durante la partita.",
                "Tende a sottovalutare i vincoli di costo e tempistica del progetto.");

        creaRuolo("JUNIOR", "Progettista Meccanico Junior",
                "Supporta il senior nelle verifiche tecniche e propone soluzioni alternative.",
                "Ricerca di riferimenti tecnici, calcoli di supporto, modellazione di base.",
                "Può chiedere una controverifica su una decisione del senior.",
                "Esperienza limitata: le sue proposte vanno sempre validate da un senior.");

        creaRuolo("QA", "Responsabile Qualità",
                "Verifica che il piano rispetti gli standard di qualità e valuta la presentazione al cliente.",
                "Controllo di conformità, criteri di accettazione, valutazione strutturata.",
                "In alcune fasi è l'unico abilitato a inviare il modulo al posto del PM.",
                "Non ha potere decisionale sulle scelte gestionali del gruppo.");

        creaRuolo("MANUFACTURING", "Manufacturing Engineer",
                "Valuta la fattibilità produttiva delle soluzioni proposte e i tempi di implementazione in linea.",
                "Processi produttivi, tempi ciclo, vincoli di stabilimento.",
                "Può segnalare come non fattibile in produzione una proposta, obbligando il gruppo a rivederla.",
                "Conosce poco le dinamiche di relazione con il cliente.");
    }

    private void creaRuolo(String codice, String nome, String missione, String competenze,
                           String superpoteri, String puntiCritici) {
        Ruolo r = new Ruolo();
        r.setCodice(codice);
        r.setNome(nome);
        r.setMissione(missione);
        r.setCompetenze(competenze);
        r.setSuperpoteri(superpoteri);
        r.setPuntiCritici(puntiCritici);
        ruoloRepository.save(r);
    }
}