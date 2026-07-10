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
}