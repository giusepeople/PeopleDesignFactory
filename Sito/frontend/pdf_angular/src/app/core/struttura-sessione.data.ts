export interface RigaStruttura {
  fase: string;
  durata: string;
  attivita: string;
  obiettivo: string;
  corrente?: boolean;
}

export const STRUTTURA_SESSIONE: RigaStruttura[] = [
  { fase: 'Intro', durata: '10 min', attivita: 'Briefing + distribuzione ruoli', obiettivo: "Comprendere il contesto e il proprio ruolo nell'ufficio tecnico" },
  { fase: 'Livello 1', durata: '20 min', attivita: 'Diagnosi Tecnica', obiettivo: 'Applicare ragionamento ingegneristico sotto pressione temporale' },
  { fase: 'Turbativa 1', durata: '5 min', attivita: 'Cambio PM tra i gruppi', obiettivo: 'Sperimentare la resilienza organizzativa' },
  { fase: 'Livello 2', durata: '20 min', attivita: 'Decisione Gestionale', obiettivo: 'Valutare trade-off costi/tempi/qualità e prendere decisioni' },
  { fase: 'Livello 3', durata: '15 min', attivita: 'Presentazione al Cliente', obiettivo: 'Comunicare sotto pressione, difendere scelte tecniche' },
  { fase: 'Livello 4', durata: '20 min', attivita: 'Ottimizzazione Finale', obiettivo: 'Integrare visione tecnica e gestionale' },
  { fase: 'Debrief', durata: '25 min', attivita: 'Riflessione + questionario', obiettivo: 'Autoconoscenza: designer vs. manager?' },
];