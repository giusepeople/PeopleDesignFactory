import { Component, signal, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { GameService, GameSummary, FaseSummary } from '../../core/game.service';
import { AuthService } from '../../core/auth.service';
import { Mascot } from '../../core/components/mascot/mascot';
import { InfoPanel } from '../../core/components/info-panel/info-panel';

@Component({
  selector: 'app-admin-dashboard',
  imports: [Mascot, InfoPanel],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit {
  private gameService = inject(GameService);
  private router = inject(Router);
  private auth = inject(AuthService);

  step = signal<'loading' | 'lista' | 'anteprima'>('loading');
  games = signal<GameSummary[]>([]);
  struttura = signal<FaseSummary[]>([]);
  loading = signal(false);
  error = signal(false);

  ngOnInit() {
    this.caricaPartite();
  }

  caricaPartite() {
    this.step.set('loading');
    this.gameService.getMyGames().subscribe({
      next: (games) => {
        this.games.set(games);
        this.step.set('lista');
      },
      error: () => {
        this.error.set(true);
        this.step.set('lista');
      },
    });
  }

  apriPannello(id: string) {
    this.router.navigate(['/admin/partita', id]);
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
    this.step.set('lista');
  }

  confermaCreazione() {
    this.loading.set(true);
    this.error.set(false);

    this.gameService.createGame().subscribe({
      next: (game) => {
        this.loading.set(false);
        this.router.navigate(['/admin/partita', game.id]);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}