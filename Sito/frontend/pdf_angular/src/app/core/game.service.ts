import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface GameSummary {
  id: string;
  codice: string;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  createGame(): Observable<GameSummary> {
    return this.http.post<GameSummary>(`${this.apiUrl}/games`, {});
  }
}