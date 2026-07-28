import { Component, OnInit, OnDestroy, HostListener, signal, inject } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { GameService } from '../../core/game.service';

type ToastType = 'success' | 'error' | 'warning';

interface Toast {
  id: number;
  type: ToastType;
  title: string;
  message?: string;
  leaving: boolean;
}

// ---------- generatore di nickname casuali a tema "officina" ----------
// Scopo (vedi note di progetto): pre-compilare un nickname divertente e
// modificabile, ragionevolmente unico all'interno di una singola partita.
const NICK_AGGETTIVI = [
  'Rapido', 'Preciso', 'Instancabile', 'Audace', 'Metodico',
  'Brillante', 'Tenace', 'Ingegnoso', 'Vulcanico', 'Impavido',
];
const NICK_SOSTANTIVI = [
  'Bullone', 'Pistone', 'Ingranaggio', 'Saldatore', 'Tornio',
  'Cricchetto', 'Rivetto', 'Cuscinetto', 'Perno', 'Compasso',
];

const LOADING_STEPS = [
  'Connessione alla partita...',
  'Verifica del PIN...',
  'Controllo del nickname...',
  'Preparazione della sessione...',
];

// durata minima della sequenza di caricamento, cosi anche una risposta
// di rete fulminea non "salta" gli step e la UX resta coerente
const LOADING_MIN_MS = 1500;
const LOADING_STEP_MS = LOADING_MIN_MS / LOADING_STEPS.length;

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, OnDestroy {
  private auth = inject(AuthService);
  private gameService = inject(GameService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  // ---------- accesso partecipanti ----------
  pin = signal('');
  nickname = signal('');
  pinError = signal(false);
  joining = signal(false);
  loadingStep = signal(0);

  private loadingInterval?: ReturnType<typeof setInterval>;

  // ---------- toast ----------
  toasts = signal<Toast[]>([]);
  private toastSeq = 0;

  // ---------- modale Game Master ----------
  showAdminModal = signal(false);
  adminUsername = signal('');
  adminPassword = signal('');
  adminError = signal(false);
  adminLoading = signal(false);

  ngOnInit() {
    this.nickname.set(this.generaNickname());

    // arrivo da un QR code proiettato dal Game Master: il link è del tipo
    // "/?pin=AB12CD" e qui precompiliamo subito il campo, sanificando
    // l'input allo stesso modo dell'inserimento manuale.
    const pinDaUrl = this.route.snapshot.queryParamMap.get('pin');
    if (pinDaUrl) {
      const pulito = pinDaUrl.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 6);
      if (pulito) this.pin.set(pulito);
    }
  }

  ngOnDestroy() {
    if (this.loadingInterval) clearInterval(this.loadingInterval);
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    if (this.showAdminModal()) this.closeAdminModal();
  }

  private generaNickname(): string {
    const agg = NICK_AGGETTIVI[Math.floor(Math.random() * NICK_AGGETTIVI.length)];
    const sost = NICK_SOSTANTIVI[Math.floor(Math.random() * NICK_SOSTANTIVI.length)];
    const numero = Math.floor(Math.random() * 90 + 10);
    return `${sost}${agg}${numero}`;
  }

  rigeneraNickname() {
    this.nickname.set(this.generaNickname());
  }

  // ---------- input ----------

  onPinInput(event: Event) {
    const value = (event.target as HTMLInputElement).value
      .toUpperCase()
      .replace(/[^A-Z0-9]/g, '')
      .slice(0, 6);
    this.pin.set(value);
    this.pinError.set(false);
  }

  onNicknameInput(event: Event) {
    this.nickname.set((event.target as HTMLInputElement).value.slice(0, 30));
  }

  // ---------- ingresso in partita ----------

  joinGame() {
    if (this.joining()) return;

    if (this.pin().length < 4 || !this.nickname().trim()) {
      this.pinError.set(true);
      this.pushToast('warning', 'Mancano dei dati', 'Inserisci il PIN della partita e il tuo nickname.');
      return;
    }

    this.pinError.set(false);
    this.joining.set(true);
    this.loadingStep.set(0);

    const partenza = Date.now();
    this.loadingInterval = setInterval(() => {
      this.loadingStep.update((s) => Math.min(s + 1, LOADING_STEPS.length - 1));
    }, LOADING_STEP_MS);

    this.gameService.joinGame(this.pin(), this.nickname().trim()).subscribe({
      next: (res) => {
        this.terminaCaricamento(partenza, () => {
          localStorage.setItem('player_session', JSON.stringify(res));
          this.pushToast('success', 'Accesso effettuato', `Ci si vede in officina, ${res.nickname}!`);
          this.joining.set(false);
          this.router.navigate(['/lobby', res.partitaId]);
        });
      },
      error: (err) => {
        this.terminaCaricamento(partenza, () => {
          this.joining.set(false);
          this.gestisciErroreIngresso(err);
        });
      },
    });
  }

  private terminaCaricamento(partenza: number, poi: () => void) {
    if (this.loadingInterval) clearInterval(this.loadingInterval);
    this.loadingStep.set(LOADING_STEPS.length - 1);
    const trascorsi = Date.now() - partenza;
    const attesa = Math.max(0, LOADING_MIN_MS - trascorsi);
    setTimeout(poi, attesa);
  }

  private gestisciErroreIngresso(err: any) {
    if (err.status === 0) {
      this.pushToast('error', 'Errore di connessione', 'Controlla la tua rete e riprova.');
    } else if (err.status >= 500) {
      this.pushToast('error', 'Server non disponibile', 'Riprova tra qualche istante.');
    } else if (err.status === 404) {
      this.pushToast('error', 'Partita non trovata', err.error ?? 'Il codice inserito non è valido.');
    } else if (err.status === 409) {
      this.pushToast('error', 'Impossibile entrare', err.error ?? 'Nickname già in uso o partita già avviata.');
    } else {
      this.pushToast('error', 'Qualcosa è andato storto', err.error ?? 'Riprova tra poco.');
    }
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

  // ---------- Game Master ----------

  openAdminModal() {
    this.showAdminModal.set(true);
  }

  closeAdminModal() {
    this.showAdminModal.set(false);
    this.adminError.set(false);
    this.adminUsername.set('');
    this.adminPassword.set('');
  }

  onAdminUsernameInput(event: Event) {
    this.adminUsername.set((event.target as HTMLInputElement).value);
  }

  onAdminPasswordInput(event: Event) {
    this.adminPassword.set((event.target as HTMLInputElement).value);
  }

  adminLogin() {
    if (!this.adminUsername() || !this.adminPassword()) {
      this.adminError.set(true);
      return;
    }

    this.adminLoading.set(true);
    this.adminError.set(false);

    this.auth.login(this.adminUsername(), this.adminPassword()).subscribe({
      next: () => {
        this.adminLoading.set(false);
        this.closeAdminModal();
        this.pushToast('success', 'Accesso Game Master', 'Bentornato nel pannello di controllo.');
        this.router.navigate(['/admin']);
      },
      error: () => {
        this.adminLoading.set(false);
        this.adminError.set(true);
        this.pushToast('error', 'Credenziali non valide', 'Controlla username e password.');
      },
    });
  }

  // ---------- helper per il template ----------

  get loadingMessage(): string {
    return LOADING_STEPS[this.loadingStep()];
  }

  get loadingSteps() {
    return LOADING_STEPS;
  }
}