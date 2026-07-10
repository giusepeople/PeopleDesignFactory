import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GameService, LobbyState } from '../../core/game.service';

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
  errorMsg = signal('');
  private pollHandle: ReturnType<typeof setInterval> | undefined;

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
    this.pollHandle = setInterval(() => this.refresh(), 3000);
  }

  ngOnDestroy() {
    if (this.pollHandle) {
      clearInterval(this.pollHandle);
    }
  }

  refresh() {
    this.gameService.getState(this.partitaId).subscribe({
      next: (s) => this.state.set(s),
      error: () => this.errorMsg.set('Impossibile aggiornare lo stato della partita.'),
    });
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