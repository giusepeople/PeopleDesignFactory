import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GameService, LobbyState, FaseCorrenteResponse } from '../../core/game.service';
import { formatSecondi } from '../../core/countdown.util';

interface PlayerSession {
  giocatoreId: string;
  partitaId: string;
  sessionToken: string;
  nickname: string;
}

@Component({
  selector: 'app-lobby',
  imports: [],
  templateUrl: './lobby.html',
  styleUrl: './lobby.css',
})
export class Lobby implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private gameService = inject(GameService);

  partitaId = '';
  session = signal<PlayerSession | null>(null);
  state = signal<LobbyState | null>(null);
  faseCorrente = signal<FaseCorrenteResponse | null>(null);
  errorMsg = signal('');
  secondiVisualizzati = signal<number | null>(null);

  private pollHandle: ReturnType<typeof setInterval> | undefined;
  private tickHandle: ReturnType<typeof setInterval> | undefined;

  ngOnInit() {
    this.partitaId = this.route.snapshot.paramMap.get('id') ?? '';

    const raw = localStorage.getItem('player_session');
    if (raw) {
      const parsed: PlayerSession = JSON.parse(raw);
      if (parsed.partitaId === this.partitaId) {
        this.session.set(parsed);
      }
    }

    this.refresh();
    this.pollHandle = setInterval(() => this.refresh(), 4000);
    this.tickHandle = setInterval(() => this.tick(), 1000);
  }

  ngOnDestroy() {
    if (this.pollHandle) clearInterval(this.pollHandle);
    if (this.tickHandle) clearInterval(this.tickHandle);
  }

  refresh() {
    this.gameService.getState(this.partitaId).subscribe({
      next: (s) => this.state.set(s),
      error: () => this.errorMsg.set('Impossibile aggiornare lo stato della partita.'),
    });

    this.gameService.getFaseCorrente(this.partitaId).subscribe({
      next: (f) => {
        this.faseCorrente.set(f);
        this.secondiVisualizzati.set(f.secondiRimanenti);
      },
      error: () => {},
    });
  }

  private tick() {
    const attuale = this.secondiVisualizzati();
    if (attuale !== null && attuale > 0) {
      this.secondiVisualizzati.set(attuale - 1);
    }
  }

  get tempoFormattato(): string {
    return formatSecondi(this.secondiVisualizzati());
  }

  get me() {
    const sess = this.session();
    const st = this.state();
    if (!sess || !st) return null;
    return st.giocatori.find((g) => g.id === sess.giocatoreId) ?? null;
  }

  get compagniDiGruppo() {
    const meG = this.me;
    const st = this.state();
    if (!meG || !st || meG.gruppoNum == null) return [];
    return st.giocatori.filter((g) => g.gruppoNum === meG.gruppoNum && g.id !== meG.id);
  }
}