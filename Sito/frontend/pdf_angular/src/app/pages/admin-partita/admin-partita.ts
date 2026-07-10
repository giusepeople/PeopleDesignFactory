import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GameService, PannelloControllo } from '../../core/game.service';

@Component({
  selector: 'app-admin-partita',
  imports: [],
  templateUrl: './admin-partita.html',
  styleUrl: './admin-partita.css',
})
export class AdminPartita implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private gameService = inject(GameService);

  partitaId = '';
  pannello = signal<PannelloControllo | null>(null);
  errorMsg = signal('');
  avviando = signal(false);
  private pollHandle: ReturnType<typeof setInterval> | undefined;

  ngOnInit() {
    this.partitaId = this.route.snapshot.paramMap.get('id') ?? '';
    this.refresh();
    this.pollHandle = setInterval(() => this.refresh(), 4000);
  }

  ngOnDestroy() {
    if (this.pollHandle) {
      clearInterval(this.pollHandle);
    }
  }

  refresh() {
    this.gameService.getPannello(this.partitaId).subscribe({
      next: (p) => this.pannello.set(p),
      error: () => this.errorMsg.set('Impossibile caricare il pannello di controllo.'),
    });
  }

  avviaPartita() {
    this.avviando.set(true);
    this.errorMsg.set('');

    this.gameService.avviaPartita(this.partitaId).subscribe({
      next: (p) => {
        this.pannello.set(p);
        this.avviando.set(false);
      },
      error: (err) => {
        this.avviando.set(false);
        this.errorMsg.set(err.error ?? 'Servono almeno 5 giocatori per avviare la partita.');
      },
    });
  }
}