import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { GameService, ModuloCorrenteResponse, DomandaModulo, RispostaInput } from '../../game.service';

@Component({
  selector: 'app-modulo-form',
  imports: [],
  templateUrl: './modulo-form.html',
  styleUrl: './modulo-form.css',
})
export class ModuloForm implements OnInit, OnDestroy {
  @Input({ required: true }) partitaId!: string;
  @Input({ required: true }) giocatoreId!: string;
  @Input({ required: true }) sessionToken!: string;

  // emette i minuti extra correnti del gruppo, cosi la pagina lobby puo' correggere il timer generale
  @Output() minutiExtraChange = new EventEmitter<number>();

  private gameService = inject(GameService);

  modulo = signal<ModuloCorrenteResponse | null>(null);
  formValues = signal<Record<string, string>>({});
  giustificazioneValues = signal<Record<string, string>>({});
  loading = signal(true);
  salvando = signal(false);
  inviando = signal(false);
  errorMsg = signal('');
  okMsg = signal('');

  private pollHandle: ReturnType<typeof setInterval> | undefined;

  ngOnInit() {
    this.refresh();
    this.pollHandle = setInterval(() => this.refresh(), 5000);
  }

  ngOnDestroy() {
    if (this.pollHandle) clearInterval(this.pollHandle);
  }

  refresh() {
    this.gameService.getModuloCorrente(this.partitaId, this.giocatoreId, this.sessionToken).subscribe({
      next: (resp) => {
        this.applyServerState(resp);
        this.loading.set(false);
        this.minutiExtraChange.emit(resp.minutiExtra ?? 0);
      },
      error: () => {
        this.loading.set(false);
        this.errorMsg.set('Impossibile caricare il foglio risposta.');
      },
    });
  }

  private applyServerState(resp: ModuloCorrenteResponse) {
    this.modulo.set(resp);

    const risposteMap = new Map(resp.risposteAttuali.map((r) => [r.domandaId, r.testoRisposta ?? '']));
    const giustMap = new Map(resp.risposteAttuali.map((r) => [r.domandaId, r.giustificazione ?? '']));
    const attualiVal = this.formValues();
    const attualiGiust = this.giustificazioneValues();
    const nextVal: Record<string, string> = {};
    const nextGiust: Record<string, string> = {};

    for (const d of resp.domande) {
      const mioCampo = this.isEditable(d, resp);

      nextVal[d.id] = mioCampo && attualiVal[d.id] !== undefined
        ? attualiVal[d.id]
        : (risposteMap.get(d.id) ?? '');

      nextGiust[d.id] = mioCampo && attualiGiust[d.id] !== undefined
        ? attualiGiust[d.id]
        : (giustMap.get(d.id) ?? '');
    }

    this.formValues.set(nextVal);
    this.giustificazioneValues.set(nextGiust);
  }

  isEditable(d: DomandaModulo, resp: ModuloCorrenteResponse | null = this.modulo()): boolean {
    if (!resp) return false;
    if (resp.invioStato === 'INVIATO' || resp.invioStato === 'APPROVATO') return false;
    if (d.restrictedRoleCodice) return resp.mioRuoloCodice === d.restrictedRoleCodice;
    return resp.sonoIoPM;
  }

  rispostaInfo(domandaId: string) {
    return this.modulo()?.risposteAttuali.find((r) => r.domandaId === domandaId) ?? null;
  }

  paragrafi(testo: string | null | undefined): string[] {
    if (!testo) return [];
    return testo.split('\n\n').filter((p) => p.trim().length > 0);
  }

  onValueChange(domandaId: string, value: string) {
    this.formValues.update((v) => ({ ...v, [domandaId]: value }));
  }

  onGiustificazioneChange(domandaId: string, value: string) {
    this.giustificazioneValues.update((v) => ({ ...v, [domandaId]: value }));
  }

  private risposteModificabili(): RispostaInput[] {
    const resp = this.modulo();
    if (!resp) return [];
    const values = this.formValues();
    const giust = this.giustificazioneValues();
    return resp.domande
      .filter((d) => this.isEditable(d))
      .map((d) => ({
        domandaId: d.id,
        testoRisposta: values[d.id] ?? '',
        giustificazione: d.richiedeGiustificazione ? (giust[d.id] ?? '') : '',
      }));
  }

  salvaBozza() {
    const risposte = this.risposteModificabili();
    if (risposte.length === 0) return;

    this.salvando.set(true);
    this.errorMsg.set('');
    this.okMsg.set('');

    this.gameService.salvaRisposteModulo(this.partitaId, this.giocatoreId, this.sessionToken, risposte).subscribe({
      next: () => {
        this.salvando.set(false);
        this.okMsg.set('Bozza salvata.');
        this.refresh();
      },
      error: (err) => {
        this.salvando.set(false);
        this.errorMsg.set(err.error ?? 'Impossibile salvare la bozza.');
      },
    });
  }

  inviaModulo() {
    const risposte = this.risposteModificabili();

    this.inviando.set(true);
    this.errorMsg.set('');
    this.okMsg.set('');

    this.gameService.inviaModulo(this.partitaId, this.giocatoreId, this.sessionToken, risposte).subscribe({
      next: () => {
        this.inviando.set(false);
        this.okMsg.set('Modulo inviato al Game Master.');
        this.refresh();
      },
      error: (err) => {
        this.inviando.set(false);
        this.errorMsg.set(err.error ?? 'Impossibile inviare il modulo.');
      },
    });
  }

  get sonoIoPM(): boolean {
    return this.modulo()?.sonoIoPM ?? false;
  }

  get bloccato(): boolean {
    const stato = this.modulo()?.invioStato;
    return stato === 'INVIATO' || stato === 'APPROVATO';
  }
}