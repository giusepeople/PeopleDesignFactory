import { Component, OnInit, OnDestroy, HostListener, computed, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { GameService, GameSummary, FaseSummary, PannelloControllo } from '../../core/game.service';
import { AuthService } from '../../core/auth.service';
import { GameCard } from '../../core/components/game-card/game-card';
import { ConfirmDialog } from '../../core/components/confirm-dialog/confirm-dialog';

type LoadState = 'loading' | 'loaded' | 'error';
type StatusFilter = 'ALL' | 'IN_ATTESA' | 'IN_CORSO' | 'TERMINATA';
type SortOption = 'data_desc' | 'data_asc' | 'giocatori_desc' | 'codice_asc';
type ToastType = 'success' | 'error' | 'warning';

interface Toast {
  id: number;
  type: ToastType;
  title: string;
  message?: string;
  leaving: boolean;
}

// intervallo di aggiornamento automatico della lista, per riflettere senza
// sforzo i cambi di stato (es. un gruppo che entra in lobby) senza dover
// premere "aggiorna": abbastanza rado da non pesare sul server
const POLL_MS = 15000;

@Component({
  selector: 'app-admin-dashboard',
  imports: [GameCard, ConfirmDialog],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit, OnDestroy {
  private gameService = inject(GameService);
  private auth = inject(AuthService);
  private router = inject(Router);

  // ---------- dati ----------
  games = signal<GameSummary[]>([]);
  loadState = signal<LoadState>('loading');

  gmNome = this.auth.gmNome;
  oggi = new Date();

  // ---------- ricerca / filtri ----------
  searchTerm = signal('');
  statusFilter = signal<StatusFilter>('ALL');
  sortBy = signal<SortOption>('data_desc');

  // ---------- sidebar mobile ----------
  sidebarAperta = signal(false);

  // ---------- drawer informazioni ----------
  drawerGame = signal<GameSummary | null>(null);
  drawerDettaglio = signal<PannelloControllo | null>(null);
  drawerLoading = signal(false);
  drawerError = signal(false);

  // ---------- eliminazione ----------
  gameDaEliminare = signal<GameSummary | null>(null);
  eliminando = signal(false);

  // ---------- creazione nuova partita ----------
  modalCreazioneAperta = signal(false);
  strutturaPreview = signal<FaseSummary[]>([]);
  strutturaLoading = signal(false);
  strutturaError = signal(false);
  creazioneLoading = signal(false);

  // ---------- toast ----------
  toasts = signal<Toast[]>([]);
  private toastSeq = 0;

  // skeleton di caricamento: solo un placeholder, non rappresenta dati reali
  skeletonItems = [1, 2, 3, 4, 5, 6];

  private pollHandle: ReturnType<typeof setInterval> | undefined;

  ngOnInit() {
    this.caricaPartite();
    this.pollHandle = setInterval(() => this.caricaPartite(true), POLL_MS);
  }

  ngOnDestroy() {
    if (this.pollHandle) clearInterval(this.pollHandle);
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    if (this.modalCreazioneAperta()) this.chiudiModalCreazione();
    else if (this.drawerGame()) this.chiudiDrawer();
  }

  // ---------- caricamento lista ----------

  caricaPartite(silenzioso = false) {
    if (!silenzioso) this.loadState.set('loading');

    this.gameService.getMyGames().subscribe({
      next: (games) => {
        this.games.set(games);
        this.loadState.set('loaded');
      },
      error: () => {
        if (!silenzioso) this.loadState.set('error');
      },
    });
  }

  // ---------- statistiche sidebar ----------

  get totalePartite(): number {
    return this.games().length;
  }

  get partiteAttive(): number {
    return this.games().filter((g) => g.status === 'IN_CORSO').length;
  }

  get partiteTerminate(): number {
    return this.games().filter((g) => g.status === 'TERMINATA').length;
  }

  get giocatoriInGioco(): number {
    return this.games()
      .filter((g) => g.status === 'IN_CORSO')
      .reduce((tot, g) => tot + (g.totaleGiocatori ?? 0), 0);
  }

  get ultimaPartita(): GameSummary | null {
    // il backend ordina già per data di creazione decrescente
    return this.games()[0] ?? null;
  }

  get oggiFormattato(): string {
    const testo = this.oggi.toLocaleDateString('it-IT', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });
    return testo.charAt(0).toUpperCase() + testo.slice(1);
  }

  // ---------- filtri / ordinamento ----------

  filteredGames = computed(() => {
    const term = this.searchTerm().trim().toUpperCase();
    const stato = this.statusFilter();
    const ordine = this.sortBy();

    let list = this.games();

    if (term) {
      list = list.filter((g) => g.codice.toUpperCase().includes(term));
    }
    if (stato !== 'ALL') {
      list = list.filter((g) => g.status === stato);
    }

    return [...list].sort((a, b) => {
      switch (ordine) {
        case 'data_asc':
          return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
        case 'giocatori_desc':
          return (b.totaleGiocatori ?? 0) - (a.totaleGiocatori ?? 0);
        case 'codice_asc':
          return a.codice.localeCompare(b.codice);
        default:
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
      }
    });
  });

  onSearchInput(event: Event) {
    this.searchTerm.set((event.target as HTMLInputElement).value);
  }

  setStatusFilter(f: StatusFilter) {
    this.statusFilter.set(f);
  }

  onSortChange(event: Event) {
    this.sortBy.set((event.target as HTMLSelectElement).value as SortOption);
  }

  get filtriAttivi(): boolean {
    return this.searchTerm().trim().length > 0 || this.statusFilter() !== 'ALL' || this.sortBy() !== 'data_desc';
  }

  azzeraFiltri() {
    this.searchTerm.set('');
    this.statusFilter.set('ALL');
    this.sortBy.set('data_desc');
  }

  // ---------- sidebar mobile ----------

  toggleSidebar() {
    this.sidebarAperta.update((v) => !v);
  }

  chiudiSidebar() {
    this.sidebarAperta.set(false);
  }

  // ---------- drawer informazioni ----------

  apriDrawer(game: GameSummary) {
    this.drawerGame.set(game);
    this.drawerDettaglio.set(null);
    this.drawerError.set(false);
    this.drawerLoading.set(true);

    this.gameService.getPannello(game.id).subscribe({
      next: (p) => {
        this.drawerDettaglio.set(p);
        this.drawerLoading.set(false);
      },
      error: () => {
        this.drawerError.set(true);
        this.drawerLoading.set(false);
      },
    });
  }

  chiudiDrawer() {
    this.drawerGame.set(null);
  }

  // ---------- eliminazione ----------

  apriElimina(game: GameSummary) {
    this.gameDaEliminare.set(game);
  }

  annullaElimina() {
    if (this.eliminando()) return;
    this.gameDaEliminare.set(null);
  }

  confermaElimina() {
    const game = this.gameDaEliminare();
    if (!game) return;

    this.eliminando.set(true);
    this.gameService.deleteGame(game.id).subscribe({
      next: () => {
        this.games.update((list) => list.filter((g) => g.id !== game.id));
        this.eliminando.set(false);
        this.gameDaEliminare.set(null);
        this.pushToast('success', 'Partita eliminata', `Il codice ${game.codice} non è più disponibile.`);
      },
      error: (err) => {
        this.eliminando.set(false);
        this.pushToast('error', 'Impossibile eliminare', err.error ?? 'Riprova tra qualche istante.');
      },
    });
  }

  // ---------- creazione nuova partita ----------

  apriModalCreazione() {
    this.chiudiSidebar();
    this.modalCreazioneAperta.set(true);
    this.strutturaLoading.set(true);
    this.strutturaError.set(false);

    this.gameService.getStruttura().subscribe({
      next: (fasi) => {
        this.strutturaPreview.set(fasi);
        this.strutturaLoading.set(false);
      },
      error: () => {
        this.strutturaError.set(true);
        this.strutturaLoading.set(false);
      },
    });
  }

  chiudiModalCreazione() {
    if (this.creazioneLoading()) return;
    this.modalCreazioneAperta.set(false);
  }

  confermaCreazione() {
    this.creazioneLoading.set(true);

    this.gameService.createGame().subscribe({
      next: (game) => {
        this.creazioneLoading.set(false);
        this.modalCreazioneAperta.set(false);
        this.pushToast('success', 'Partita creata', `Codice ${game.codice} pronto per la lobby.`);
        this.caricaPartite();
      },
      error: () => {
        this.creazioneLoading.set(false);
        this.pushToast('error', 'Errore', 'Impossibile creare la partita. Riprova.');
      },
    });
  }

  // ---------- navigazione ----------

  apriPartita(game: GameSummary) {
    this.router.navigate(['/admin/partita', game.id]);
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/']);
  }

  // ---------- toast ----------

  pushToast(type: ToastType, title: string, message?: string) {
    const id = ++this.toastSeq;
    this.toasts.update((list) => [...list, { id, type, title, message, leaving: false }]);
    setTimeout(() => this.dismissToast(id), 4500);
  }

  dismissToast(id: number) {
    this.toasts.update((list) => list.map((t) => (t.id === id ? { ...t, leaving: true } : t)));
    setTimeout(() => {
      this.toasts.update((list) => list.filter((t) => t.id !== id));
    }, 260);
  }

  // ---------- helper template ----------

  etichettaStato(stato: string): string {
    switch (stato) {
      case 'IN_ATTESA': return 'In attesa';
      case 'IN_CORSO': return 'In corso';
      case 'TERMINATA': return 'Terminata';
      case 'LAVORANDO': return 'Al lavoro';
      case 'PRONTO': return 'Pronto';
      case 'INVIATO': return 'In revisione';
      case 'APPROVATO': return 'Approvato';
      case 'RIFIUTATO': return 'Rifiutato';
      default: return stato;
    }
  }
}