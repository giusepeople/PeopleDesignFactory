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
                "Sei il collante del team. Non sei necessariamente il più bravo tecnicamente, ma sei " +
                        "quello che fa funzionare tutto. Coordini, decidi le priorità, gestisci le tensioni " +
                        "e sei la voce del team verso l'esterno.",
                String.join("\n",
                        "Sai leggere un Gantt e costruire un piano d'azione in poco tempo",
                        "Conosci i rischi di progetto e sai come comunicarli al cliente",
                        "Hai esperienza nella gestione delle risorse e dei fornitori",
                        "Sai facilitare riunioni anche quando il tempo manca"),
                String.join("\n",
                        "POTERE DI DECISIONE FINALE: in caso di disaccordo nel team (dopo 2 minuti di discussione), sei tu che decidi. La tua parola è quella che va sul foglio risposta.",
                        "POTERE DI DELEGA: puoi assegnare compiti specifici agli altri membri e spostare chi fa cosa durante la sfida.",
                        "POTERE DI COMUNICAZIONE: solo tu presenti al 'cliente' (Game Master) nel Livello 3. Gli altri ti supportano ma non parlano."),
                String.join("\n",
                        "Rischio: potresti essere così preso dal coordinare da non ascoltare davvero le soluzioni tecniche dei tuoi colleghi. Ascolta prima di decidere.",
                        "Alla TURBATIVA 1 sarai spostato in un altro gruppo: preparati a riprendere velocemente il controllo di una situazione che non conosci."));

        creaRuolo("SENIOR", "Progettista Meccanico Senior",
                "Sei il riferimento tecnico del team. Hai esperienza su questo tipo di componenti e il tuo " +
                        "giudizio tecnico pesa più di quello degli altri. La tua sfida è trasformare la " +
                        "conoscenza tecnica in soluzioni praticabili.",
                String.join("\n",
                        "Conosci i meccanismi di cedimento per fatica e le relative contromisure",
                        "Sai interpretare disegni tecnici e tolleranze",
                        "Hai lavorato con materiali metallici e trattamenti termici",
                        "Sai valutare la fattibilità di una modifica progettuale"),
                String.join("\n",
                        "POTERE DI VETO TECNICO: se una soluzione proposta è tecnicamente impossibile o pericolosa, puoi bloccarla. Devi però spiegare brevemente il motivo al team.",
                        "ANALISI RAPIDA: nel Livello 1 il tuo foglio risposta ha un campo 'Analisi Senior' che vale punti bonus se compilato con argomentazioni tecniche valide.",
                        "CONSULENZA TRASVERSALE: gli altri gruppi possono chiederti una 'consulenza' (30 secondi), ma il PM deve approvare la richiesta."),
                String.join("\n",
                        "Rischio: potresti essere così sicuro della tua soluzione da non considerare i vincoli di costo e tempo che il PM e il Manufacturing Engineer ti segnalano.",
                        "La perfezione tecnica a volte deve cedere alla fattibilità. Il cliente vuole una soluzione in 48h, non la soluzione perfetta."));

        creaRuolo("JUNIOR", "Progettista Meccanico Junior",
                "Sei entrato in azienda da poco ma porti idee fresche e non hai ancora i 'paraocchi' " +
                        "dell'esperienza. Il tuo punto di forza è la curiosità e la voglia di capire. " +
                        "Fai domande, proponi alternative, non dare niente per scontato.",
                String.join("\n",
                        "Conosci la teoria dei meccanismi di trasmissione e la resistenza dei materiali",
                        "Sai usare software CAD/FEM (SolidWorks, ANSYS) per simulazioni rapide",
                        "Sei aggiornato sulle ultime normative e best practice del settore",
                        "Puoi fare calcoli veloci e stime di primo approccio"),
                String.join("\n",
                        "ANALISI FEM EXPRESS: nel Livello 1 puoi proporre una 'simulazione concettuale veloce' (descritta a parole) per supportare l'analisi del Senior.",
                        "RICERCA ALTERNATIVA: nel Livello 2, se la soluzione del team è bloccata, puoi proporre UNA soluzione alternativa aggiuntiva da valutare.",
                        "DOMANDA SCOMODA: una volta durante la sessione puoi fare una 'Domanda Scomoda' che obbliga il team a fermarsi 60 secondi e riconsiderare un'assunzione."),
                String.join("\n",
                        "Rischio: potresti essere in soggezione davanti al Senior e non proporre le tue idee. Ricorda: in questo gioco la tua voce vale quanto la sua.",
                        "Al contrario: non proporre soluzioni troppo teoriche o che richiedono mesi di sviluppo. Il tempo è 48 ore."));

        creaRuolo("QA", "Responsabile Qualità",
                "Sei la coscienza del team. Non ti interessa solo che il problema sia risolto: ti interessa " +
                        "che sia risolto bene, in modo tracciabile e dimostrabile al cliente. In un'azienda " +
                        "automotive, la qualità non è opzionale.",
                String.join("\n",
                        "Conosci il processo APQP e le norme IATF 16949 / ISO 9001",
                        "Sai strutturare un'analisi delle cause (8D, Ishikawa, FMEA)",
                        "Hai esperienza con i report non conformità (NC) e i piani correttivi (CAPA)",
                        "Sai cosa si aspetta un cliente OEM da una relazione tecnica di risposta"),
                String.join("\n",
                        "SCHEDA 8D EXPRESS: nel Livello 1, compili in parallelo una scheda 8D semplificata (D1-D4) che il team può usare come struttura per la risposta al cliente.",
                        "STOP QUALITY: una volta puoi fermare il team se sta per essere consegnata una risposta che 'non reggerebbe ad un audit'. Hai 90 secondi per spiegare il problema.",
                        "FORMATO RISPOSTA: sei tu che compili il foglio risposta finale del Livello 3 (la presentazione), assicurandoti che sia formalmente corretto e professionale."),
                String.join("\n",
                        "Rischio: il perfezionismo da QA può rallentare il team in situazioni di emergenza. A volte 'good enough now' vale più di 'perfect later'.",
                        "Devi trovare l'equilibrio tra rigore procedurale e velocità decisionale. Un report 8D incompleto ma fatto è meglio di uno perfetto non consegnato."));

        creaRuolo("MANUFACTURING", "Manufacturing Engineer",
                "Sei il ponte tra la progettazione e la produzione. Sai cosa è realizzabile in officina, a " +
                        "che costo e in quanto tempo. Quando i tuoi colleghi propongono soluzioni bellissime " +
                        "sulla carta, sei tu che dici 'sì, ma come la facciamo?'",
                String.join("\n",
                        "Conosci i processi produttivi: tornitura, rettifica, trattamenti termici, controlli CND",
                        "Sai stimare i tempi ciclo e i costi di lavorazione",
                        "Hai esperienza con i fornitori di lavorazioni meccaniche e trattamenti",
                        "Sai valutare l'impatto di una modifica progettuale sulla linea di produzione"),
                String.join("\n",
                        "STIMA COSTI & TEMPI: nel Livello 2 fornisci la stima ufficiale di costo e tempo per ogni soluzione proposta. Il team non può scegliere senza la tua valutazione.",
                        "FORNITORE DI EMERGENZA: nel Livello 2 hai accesso a una 'lista fornitori di emergenza' (fornita dal GM) con 3 opzioni rapide per materiali o lavorazioni urgenti.",
                        "FATTIBILITÀ ESPRESSA: nel Livello 4 puoi proporre un'ottimizzazione di processo con risparmio stimato senza passare per il PM."),
                String.join("\n",
                        "Rischio: potresti concentrarti solo su ciò che è già stato fatto prima ('si è sempre fatto così') e resistere alle modifiche progettuali necessarie.",
                        "In una crisi, la flessibilità produttiva vale oro. Sii disposto a esplorare soluzioni non standard se la situazione lo richiede."));
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