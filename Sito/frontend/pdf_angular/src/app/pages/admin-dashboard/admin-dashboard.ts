import { Component, signal, inject } from '@angular/core';
import { GameService, GameSummary } from '../../core/game.service';

@Component({
  selector: 'app-admin-dashboard',
  imports: [],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard {
  private gameService = inject(GameService);

  currentGame = signal<GameSummary | null>(null);
  loading = signal(false);
  error = signal(false);

  createGame() {
    this.loading.set(true);
    this.error.set(false);

    this.gameService.createGame().subscribe({
      next: (game) => {
        this.currentGame.set(game);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }
}