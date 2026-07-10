import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GameService, PannelloControllo, FaseCorrenteResponse } from '../../core/game.service';
import { formatSecondi } from '../../core/countdown.util';

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
  faseCorrente = signal<FaseCorrenteResponse | null>(null);
  errorMsg = signal('');
  avviando = signal(false);
  avanzando = signal(false);
  secondiVisualizzati = signal<number | null>(null);

  private pollHandle: ReturnType<typeof setInterval> | undefined;
  private tickHandle: ReturnType<typeof setInterval> | undefined;

  ngOnInit() {
    this.partitaId = this.route.snapshot.paramMap.get('id') ?? '';
    this.refresh();
    this.pollHandle = setInterval(() => this.refresh(), 4000);
    this.tickHandle = setInterval(() => this.tick(), 1000);
  }

  ngOnDestroy() {
    if (this.pollHandle) clearInterval(this.pollHandle);
    if (this.tickHandle) clearInterval(this.tickHandle);
  }

  refresh() {
    this.gameService.getPannello(this.partitaId).subscribe({
      next: (p) => this.pannello.set(p),
      error: () => this.errorMsg.set('Impossibile caricare il pannello di controllo.'),
    });

    if (this.pannello()?.status === 'IN_CORSO') {
      this.gameService.getFaseCorrente(this.partitaId).subscribe({
        next: (f) => {
          this.faseCorrente.set(f);
          this.secondiVisualizzati.set(f.secondiRimanenti);
        },
        error: () => {},
      });
    }
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

  avviaPartita() {
    this.avviando.set(true);
    this.errorMsg.set('');

    this.gameService.avviaPartita(this.partitaId).subscribe({
      next: (p) => {
        this.pannello.set(p);
        this.avviando.set(false);
        this.refresh();
      },
      error: (err) => {
        this.avviando.set(false);
        this.errorMsg.set(err.error ?? 'Servono almeno 5 giocatori per avviare la partita.');
      },
    });
  }

  avanzaFase() {
    this.avanzando.set(true);
    this.errorMsg.set('');

    this.gameService.avanzaFase(this.partitaId).subscribe({
      next: (p) => {
        this.pannello.set(p);
        this.avanzando.set(false);
        this.refresh();
      },
      error: (err) => {
        this.avanzando.set(false);
        this.errorMsg.set(err.error ?? 'Impossibile avanzare alla fase successiva.');
      },
    });
  }
}