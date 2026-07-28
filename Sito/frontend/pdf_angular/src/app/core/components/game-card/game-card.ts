import { Component, Input, Output, EventEmitter } from '@angular/core';
import { GameSummary } from '../../game.service';

@Component({
  selector: 'app-game-card',
  imports: [],
  templateUrl: './game-card.html',
  styleUrl: './game-card.css',
})
export class GameCard {
  @Input({ required: true }) game!: GameSummary;

  @Output() info = new EventEmitter<void>();
  @Output() remove = new EventEmitter<void>();
  @Output() open = new EventEmitter<void>();
  @Output() fullscreen = new EventEmitter<void>();

  get statoLabel(): string {
    switch (this.game.status) {
      case 'IN_ATTESA': return 'In attesa';
      case 'IN_CORSO': return 'In corso';
      case 'TERMINATA': return 'Terminata';
      default: return this.game.status;
    }
  }

  get dataFormattata(): string {
    if (!this.game.createdAt) return '—';
    return new Date(this.game.createdAt).toLocaleDateString('it-IT', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  }

  get oraFormattata(): string {
    if (!this.game.createdAt) return '';
    return new Date(this.game.createdAt).toLocaleTimeString('it-IT', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}