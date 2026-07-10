import { Component, signal, inject, OnInit } from '@angular/core';
import { GameService, GameSummary, FaseSummary } from '../../core/game.service';

@Component({
  selector: 'app-admin-dashboard',
  imports: [],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit {
  private gameService = inject(GameService);

  step = signal<'loading' | 'idle' | 'anteprima' | 'creata'>('loading');
  currentGame = signal<GameSummary | null>(null);
  struttura = signal<FaseSummary[]>([]);
  loading = signal(false);
  error = signal(false);

  ngOnInit() {
    this.gameService.getMyGames().subscribe({
      next: (games) => {
        const partitaAttiva = games.find((g) => g.status !== 'TERMINATA');
        if (partitaAttiva) {
          this.currentGame.set(partitaAttiva);
          this.step.set('creata');
        } else {
          this.step.set('idle');
        }
      },
      error: () => {
        // se il fetch fallisce non blocchiamo il GM: puo' comunque crearne una nuova
        this.step.set('idle');
      },
    });
  }

  apriAnteprima() {
    this.loading.set(true);
    this.error.set(false);

    this.gameService.getStruttura().subscribe({
      next: (fasi) => {
        this.struttura.set(fasi);
        this.step.set('anteprima');
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }

  annullaAnteprima() {
    this.step.set('idle');
  }

  confermaCreazione() {
    this.loading.set(true);
    this.error.set(false);

    this.gameService.createGame().subscribe({
      next: (game) => {
        this.currentGame.set(game);
        this.step.set('creata');
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }
}