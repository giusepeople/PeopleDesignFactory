import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

interface LoginResponse {
  token: string;
  nome: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080';
  private readonly tokenKey = 'gm_token';
  private readonly nomeKey = 'gm_nome';

  isLoggedIn = signal(!!localStorage.getItem(this.tokenKey));
  gmNome = signal<string | null>(localStorage.getItem(this.nomeKey));

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/auth/login`, { username, password })
      .pipe(
        tap((res) => {
          localStorage.setItem(this.tokenKey, res.token);
          localStorage.setItem(this.nomeKey, res.nome);
          this.isLoggedIn.set(true);
          this.gmNome.set(res.nome);
        })
      );
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.nomeKey);
    this.isLoggedIn.set(false);
    this.gmNome.set(null);
  }
}