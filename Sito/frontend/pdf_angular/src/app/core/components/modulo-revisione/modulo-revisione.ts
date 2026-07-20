import { Component, Input, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { GameService, InvioModuloGm, GruppoDettaglio } from '../../game.service';

@Component({
  selector: 'app-modulo-revisione',
  imports: [],
  templateUrl: './modulo-revisione.html',
  styleUrl: './modulo-revisione.css',
})
export class ModuloRevisione implements OnInit, OnDestroy {
  @Input({ required: true }) partitaId!: string;
  @Input() gruppiDettaglio: GruppoDettaglio[] = [];

  private gameService = inject(GameService);

  gruppi = signal<InvioModuloGm[]>([]);
  loading = signal(true);
  errorMsg = signal('');
  processando = signal<string | null>(null);

  vedendoGruppo = signal<string | null>(null);
  infoGruppo = signal<string | null>(null);
  gruppoInRifiuto = signal<string | null>(null);
  motivoRifiuto = signal('');
  minutiExtra = signal(5);

  private pollHandle: ReturnType<typeof setInterval> | undefined;

  ngOnInit() {
    this.refresh();
    this.pollHandle = setInterval(() => this.refresh(), 4000);
  }

  ngOnDestroy() {
    if (this.pollHandle) clearInterval(this.pollHandle);
  }

  refresh() {
    this.gameService.getRevisioneModuli(this.partitaId).subscribe({
      next: (gruppi) => {
        this.gruppi.set(gruppi);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.errorMsg.set('Impossibile caricare le consegne dei gruppi.');
      },
    });
  }

  membriDi(gruppoId: string) {
    return this.gruppiDettaglio.find((g) => g.id === gruppoId)?.giocatori ?? [];
  }

  formatTempo(secondi: number | null): string {
    if (secondi === null || secondi === undefined) return '--:--';
    const m = Math.floor(secondi / 60);
    const s = secondi % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  }

  apriVedi(invio: InvioModuloGm) { this.vedendoGruppo.set(invio.gruppoId); }
  chiudiVedi() { this.vedendoGruppo.set(null); }

  apriInfo(invio: InvioModuloGm) { this.infoGruppo.set(invio.gruppoId); }
  chiudiInfo() { this.infoGruppo.set(null); }

  approva(invio: InvioModuloGm) {
    if (!invio.invioId) return;
    this.processando.set(invio.invioId);
    this.gameService.approvaModulo(this.partitaId, invio.invioId).subscribe({
      next: () => {
        this.processando.set(null);
        this.refresh();
      },
      error: (err) => {
        this.processando.set(null);
        this.errorMsg.set(err.error ?? 'Impossibile approvare il modulo.');
      },
    });
  }

  apriRifiuta(invio: InvioModuloGm) {
    this.gruppoInRifiuto.set(invio.gruppoId);
    this.motivoRifiuto.set('');
    this.minutiExtra.set(5);
  }

  annullaRifiuta() {
    this.gruppoInRifiuto.set(null);
  }

  confermaRifiuta(invio: InvioModuloGm) {
    if (!invio.invioId || !this.motivoRifiuto().trim()) return;

    this.processando.set(invio.invioId);
    this.gameService
      .rifiutaModulo(this.partitaId, invio.invioId, this.motivoRifiuto().trim(), this.minutiExtra())
      .subscribe({
        next: () => {
          this.processando.set(null);
          this.gruppoInRifiuto.set(null);
          this.refresh();
        },
        error: (err) => {
          this.processando.set(null);
          this.errorMsg.set(err.error ?? 'Impossibile rifiutare il modulo.');
        },
      });
  }

  onMotivoInput(event: Event) {
    this.motivoRifiuto.set((event.target as HTMLTextAreaElement).value);
  }

  onMinutiInput(event: Event) {
    this.minutiExtra.set(Number((event.target as HTMLInputElement).value) || 0);
  }

  etichettaStato(stato: string): string {
    switch (stato) {
      case 'BOZZA': return 'Non ancora inviato';
      case 'INVIATO': return 'In attesa di revisione';
      case 'APPROVATO': return 'Approvato';
      case 'RIFIUTATO': return 'Rifiutato';
      default: return stato;
    }
  }
}