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
  myGames = signal<GameSummary[]>([]);
  loading = signal(false);
  error = signal(false);

  ngOnInit() {
    this.gameService.getMyGames().subscribe({
      next: (games) => this.myGames.set(games),
      error: () => {}, // silenzioso, non blocca la creazione di una nuova partita
    });
  }

  createGame() {
    this.loading.set(true);
    this.error.set(false);

    this.gameService.createGame().subscribe({
      next: (game) => {
        this.currentGame.set(game);
        this.myGames.update((list) => [game, ...list]);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      },
    });
  }

    resumeGame(game: GameSummary) {
    this.currentGame.set(game);
  }
}