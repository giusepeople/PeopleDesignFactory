import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GameService, LobbyState, FaseCorrenteResponse, Ruolo } from '../../core/game.service';
import { formatSecondi, CountdownSync } from '../../core/countdown.util';
import { InfoPanel } from '../../core/components/info-panel/info-panel';
import { Mascot } from '../../core/components/mascot/mascot';
import { Gauge } from '../../core/components/gauge/gauge';
import { ModuloForm } from '../../core/components/modulo-form/modulo-form';

interface PlayerSession {
  giocatoreId: string;
  partitaId: string;
  sessionToken: string;
  nickname: string;
}

const ORDINE_RUOLI = ['PM', 'SENIOR', 'JUNIOR', 'QA', 'MANUFACTURING'];

@Component({
  selector: 'app-lobby',
  imports: [InfoPanel, Mascot, Gauge, ModuloForm],
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

  ruoli = signal<Ruolo[]>([]);
  inviandoPronto = signal(false);

  private countdown = new CountdownSync();
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

    this.gameService.getRuoli().subscribe({
      next: (ruoli) => {
        const ordinati = [...ruoli].sort(
          (a, b) => ORDINE_RUOLI.indexOf(a.codice) - ORDINE_RUOLI.indexOf(b.codice)
        );
        this.ruoli.set(ordinati);
      },
      error: () => {},
    });

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
        this.countdown.aggiorna(f.faseIniziataIl, f.fase?.durataMinuti, f.serverTimestamp);
        this.secondiVisualizzati.set(this.countdown.secondiRimanenti());
      },
      error: () => {},
    });
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

  get mioGruppoStato(): string | null {
    const meG = this.me;
    const st = this.state();
    if (!meG || !st || meG.gruppoNum == null) return null;
    return st.gruppi.find((g) => g.teamNum === meG.gruppoNum)?.stato ?? null;
  }

  get sonoPM(): boolean {
    return this.me?.ruoloCodice === 'PM';
  }

  get gruppiPronti(): number {
    return this.state()?.gruppi.filter((g) => g.stato === 'PRONTO').length ?? 0;
  }

  get totaleGruppi(): number {
    return this.state()?.gruppi.length ?? 0;
  }

  segnalaPronto() {
    const sess = this.session();
    if (!sess) return;

    this.inviandoPronto.set(true);
    this.gameService.segnalaPronto(this.partitaId, sess.giocatoreId, sess.sessionToken).subscribe({
      next: () => {
        this.inviandoPronto.set(false);
        this.refresh();
      },
      error: (err) => {
        this.inviandoPronto.set(false);
        this.errorMsg.set(err.error ?? 'Impossibile segnalare che il gruppo è pronto.');
      },
    });
  }

  bulletList(testo: string | null | undefined): string[] {
    if (!testo) return [];
    return testo.split('\n').filter((riga) => riga.trim().length > 0);
  }

  paragrafi(testo: string | null | undefined): string[] {
    if (!testo) return [];
    return testo.split('\n\n').filter((p) => p.trim().length > 0);
  }
}