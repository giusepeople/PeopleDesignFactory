import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GameService, PannelloControllo, FaseCorrenteResponse } from '../../core/game.service';
import { formatSecondi, CountdownSync } from '../../core/countdown.util';
import { InfoPanel } from '../../core/components/info-panel/info-panel';
import { Gauge } from '../../core/components/gauge/gauge';
import { ModuloRevisione } from '../../core/components/modulo-revisione/modulo-revisione';

@Component({
  selector: 'app-admin-partita',
  imports: [InfoPanel, Gauge, ModuloRevisione],
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
  showConfermaAvanza = signal(false);

  private countdown = new CountdownSync();
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
          this.countdown.aggiorna(f.faseIniziataIl, f.fase?.durataMinuti, f.serverTimestamp);
          this.secondiVisualizzati.set(this.countdown.secondiRimanenti());
        },
        error: () => {},
      });
    }
  }

  private tick() {
    this.secondiVisualizzati.set(this.countdown.secondiRimanenti());
  }

  get tempoFormattato(): string {
    return formatSecondi(this.secondiVisualizzati());
  }

  get percentTrascorso(): number {
    const rimanenti = this.secondiVisualizzati();
    const durata = this.faseCorrente()?.fase?.durataMinuti;
    if (rimanenti === null || rimanenti === undefined || !durata) return 0;
    const totale = durata * 60;
    if (totale <= 0) return 0;
    return Math.min(100, Math.max(0, ((totale - rimanenti) / totale) * 100));
  }

  get gruppiPronti(): number {
    return this.pannello()?.gruppi.filter((g) => g.stato === 'PRONTO' || g.stato === 'APPROVATO').length ?? 0;
  }

  get totaleGruppi(): number {
    return this.pannello()?.gruppi.length ?? 0;
  }

  get gruppiNonPronti(): string[] {
    return (this.pannello()?.gruppi ?? [])
      .filter((g) => g.stato !== 'PRONTO' && g.stato !== 'APPROVATO')
      .map((g) => `Gruppo ${g.teamNum} (${this.etichettaStato(g.stato)})`);
  }

  private etichettaStato(stato: string): string {
    switch (stato) {
      case 'LAVORANDO': return 'sta lavorando';
      case 'INVIATO': return 'in attesa di revisione';
      case 'RIFIUTATO': return 'modulo rifiutato';
      case 'APPROVATO': return 'modulo approvato';
      default: return stato;
    }
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

  richiediAvanzaFase() {
    if (this.pannello()?.tuttiGruppiPronti) {
      this.avanzaFase();
    } else {
      this.showConfermaAvanza.set(true);
    }
  }

  annullaAvanzaFase() {
    this.showConfermaAvanza.set(false);
  }

  confermaAvanzaFase() {
    this.showConfermaAvanza.set(false);
    this.avanzaFase();
  }

  private avanzaFase() {
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