import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';



export interface GameSummary {
  id: string;
  codice: string;
  status: string;
  createdAt: string;
  totaleGiocatori: number;
  totaleGruppi: number;
}

export interface CreatedGame {
  id: string;
  codice: string;
  status: string;
}

export interface DomandaSummary {
  id: string;
  type: string;
  text: string;
  rispostaRistretta: boolean;
}

export interface ModuloSummary {
  id: string;
  titolo: string;
  domande: DomandaSummary[];
}

export interface FaseSummary {
  id: string;
  ordinal: number;
  nome: string;
  tipo: string;
  durataMinuti: number;
  modulo: ModuloSummary | null;
}

export interface JoinResponse {
  giocatoreId: string;
  partitaId: string;
  sessionToken: string;
  nickname: string;
}

export interface GiocatoreLobby {
  id: string;
  nickname: string;
  gruppoNum: number | null;
  ruoloNome: string | null;
  ruoloCodice: string | null;
}

export interface LobbyState {
  partitaId: string;
  codice: string;
  status: string;
  totaleGiocatori: number;
  giocatori: GiocatoreLobby[];
  gruppi: GruppoStato[];
}

export interface GiocatoreDettaglio {
  id: string;
  nickname: string;
  ruoloNome: string | null;
  ruoloCodice: string | null;
}

export interface GruppoDettaglio {
  id: string;
  teamNum: number;
  stato: string;
  giocatori: GiocatoreDettaglio[];
}

export interface FaseCorrente {
  id: string;
  ordinal: number;
  nome: string;
  tipo: string;
  durataMinuti: number;
}

export interface PannelloControllo {
  id: string;
  codice: string;
  status: string;
  totaleGiocatori: number;
  faseAttuale: FaseCorrente | null;
  faseIniziataIl: string | null;
  gruppi: GruppoDettaglio[];
  giocatoriSenzaGruppo: GiocatoreDettaglio[];
  tuttiGruppiPronti: boolean;
}

export interface DatoBriefing {
  label: string;
  valore: string;
}

export interface FaseCorrenteResponse {
  partitaStatus: string;
  fase: FaseCorrente | null;
  faseIniziataIl: string | null;
  secondiRimanenti: number | null;
  contenutoTesto: string | null;
  dati: DatoBriefing[];
  opzioni: OpzioneLivello[];
  haComplicazione: boolean;
  complicazioneVisibile: boolean;
  complicazioneTesto: string | null;
  serverTimestamp: string | null;
}

export interface OpzioneLivello {
  valore: string;
  titolo: string;
  descrizione: string;
  costoStimato: string;
  tempo: string;
  rischio: string;
}

export interface Ruolo {
  id: string;
  codice: string;
  nome: string;
  missione: string;
  competenze: string;
  superpoteri: string;
  puntiCritici: string;
}

export interface GruppoStato {
  id: string;
  teamNum: number;
  stato: string;
}

// ---------- Foglio Risposta di ogni Livello ----------

export interface Opzione {
  valore: string;
  etichetta: string;
}

export interface DomandaModulo {
  id: string;
  orderIndex: number;
  type: string;
  text: string;
  opzioni: Opzione[] | null;
  restrictedRoleCodice: string | null;
  restrictedRoleNome: string | null;
  assegnataARuoloNome: string;
  richiedeGiustificazione: boolean;
}

export interface RispostaEsistente {
  domandaId: string;
  testoRisposta: string | null;
  giustificazione: string | null;
  rispostaPresente: boolean;
  corretta: boolean | null;
  hintDaMostrare: string | null;
}

export interface ModuloCorrenteResponse {
  moduloId: string;
  titolo: string;
  contenutoTesto: string | null;
  dati: DatoBriefing[];
  opzioni: OpzioneLivello[];
  complicazioneTesto: string | null;
  domande: DomandaModulo[];
  risposteAttuali: RispostaEsistente[];
  invioStato: string;
  motivoRifiuto: string | null;
  minutiExtra: number;
  sonoIoPM: boolean;
  mioRuoloCodice: string | null;
  secondiRimanenti: number | null;
  serverTimestamp: string | null;
}

export interface RispostaInput {
  domandaId: string;
  testoRisposta: string;
  giustificazione: string;
}

export interface RispostaGm {
  domandaId: string;
  orderIndex: number;
  domandaTesto: string;
  tipo: string;
  testoRisposta: string | null;
  giustificazione: string | null;
  corretta: boolean | null;
  opzioneCorretta: string | null;
  hintText: string | null;
  rispostoDaNickname: string | null;
}

export interface InvioModuloGm {
  invioId: string | null;
  gruppoId: string;
  teamNum: number;
  statoGruppo: string;
  statoInvio: string;
  inviatoIl: string | null;
  motivoRifiuto: string | null;
  minutiExtra: number;
  secondiRimanenti: number | null;
  risposte: RispostaGm[];
}

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  createGame(): Observable<CreatedGame> {
    return this.http.post<CreatedGame>(`${this.apiUrl}/games`, {});
  }

  getStruttura(): Observable<FaseSummary[]> {
    return this.http.get<FaseSummary[]>(`${this.apiUrl}/games/struttura`);
  }

  getMyGames(): Observable<GameSummary[]> {
    return this.http.get<GameSummary[]>(`${this.apiUrl}/games`);
  }

  deleteGame(partitaId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/games/${partitaId}`);
  }

  joinGame(codice: string, nickname: string): Observable<JoinResponse> {
    return this.http.post<JoinResponse>(`${this.apiUrl}/games/join`, { codice, nickname });
  }

  getState(partitaId: string): Observable<LobbyState> {
    return this.http.get<LobbyState>(`${this.apiUrl}/games/${partitaId}/state`);
  }

  avviaPartita(partitaId: string): Observable<PannelloControllo> {
    return this.http.post<PannelloControllo>(`${this.apiUrl}/games/${partitaId}/avvia`, {});
  }

  getPannello(partitaId: string): Observable<PannelloControllo> {
    return this.http.get<PannelloControllo>(`${this.apiUrl}/games/${partitaId}/pannello`);
  }

  getFaseCorrente(partitaId: string): Observable<FaseCorrenteResponse> {
    return this.http.get<FaseCorrenteResponse>(`${this.apiUrl}/games/${partitaId}/fase-corrente`);
  }

  avanzaFase(partitaId: string): Observable<PannelloControllo> {
    return this.http.post<PannelloControllo>(`${this.apiUrl}/games/${partitaId}/avanza-fase`, {});
  }

  attivaComplicazione(partitaId: string): Observable<PannelloControllo> {
    return this.http.post<PannelloControllo>(`${this.apiUrl}/games/${partitaId}/attiva-complicazione`, {});
  }

  getRuoli(): Observable<Ruolo[]> {
    return this.http.get<Ruolo[]>(`${this.apiUrl}/ruoli`);
  }

  segnalaPronto(partitaId: string, giocatoreId: string, sessionToken: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/games/${partitaId}/pronto`, { giocatoreId, sessionToken });
  }

  // ---------- Foglio Risposta (giocatore) ----------

  getModuloCorrente(partitaId: string, giocatoreId: string, sessionToken: string): Observable<ModuloCorrenteResponse> {
    return this.http.get<ModuloCorrenteResponse>(`${this.apiUrl}/games/${partitaId}/modulo-corrente`, {
      params: { giocatoreId, sessionToken },
    });
  }

  salvaRisposteModulo(
    partitaId: string,
    giocatoreId: string,
    sessionToken: string,
    risposte: RispostaInput[]
  ): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/games/${partitaId}/modulo-corrente/salva`, {
      giocatoreId,
      sessionToken,
      risposte,
    });
  }

  inviaModulo(
    partitaId: string,
    giocatoreId: string,
    sessionToken: string,
    risposte: RispostaInput[]
  ): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/games/${partitaId}/modulo-corrente/invia`, {
      giocatoreId,
      sessionToken,
      risposte,
    });
  }

  // ---------- Foglio Risposta (Game Master) ----------

  getRevisioneModuli(partitaId: string): Observable<InvioModuloGm[]> {
    return this.http.get<InvioModuloGm[]>(`${this.apiUrl}/games/${partitaId}/moduli/revisione`);
  }

  approvaModulo(partitaId: string, invioId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/games/${partitaId}/moduli/${invioId}/approva`, {});
  }

  rifiutaModulo(partitaId: string, invioId: string, motivoRifiuto: string, minutiExtra: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/games/${partitaId}/moduli/${invioId}/rifiuta`, {
      motivoRifiuto,
      minutiExtra,
    });
  }
}