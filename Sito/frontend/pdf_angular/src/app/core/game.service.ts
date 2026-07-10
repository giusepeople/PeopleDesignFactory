import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface GameSummary {
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
  serverTimestamp: string | null;
}

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  createGame(): Observable<GameSummary> {
    return this.http.post<GameSummary>(`${this.apiUrl}/games`, {});
  }

  getStruttura(): Observable<FaseSummary[]> {
    return this.http.get<FaseSummary[]>(`${this.apiUrl}/games/struttura`);
  }

  getMyGames(): Observable<GameSummary[]> {
    return this.http.get<GameSummary[]>(`${this.apiUrl}/games`);
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
}