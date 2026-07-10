import { Component, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { GameService } from '../../core/game.service';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private auth = inject(AuthService);
  private gameService = inject(GameService);
  private router = inject(Router);

  // --- accesso giocatore ---
  pin = signal('');
  nickname = signal('');
  pinError = signal(false);
  joinErrorMsg = signal('');
  joining = signal(false);

  onPinInput(event: Event) {
    const value = (event.target as HTMLInputElement).value
      .toUpperCase()
      .replace(/[^A-Z0-9]/g, '')
      .slice(0, 6);
    this.pin.set(value);
    this.pinError.set(false);
    this.joinErrorMsg.set('');
  }

  onNicknameInput(event: Event) {
    this.nickname.set((event.target as HTMLInputElement).value.slice(0, 30));
    this.joinErrorMsg.set('');
  }

  joinGame() {
    if (this.pin().length < 4 || !this.nickname().trim()) {
      this.pinError.set(true);
      return;
    }

    this.joining.set(true);
    this.joinErrorMsg.set('');

    this.gameService.joinGame(this.pin(), this.nickname().trim()).subscribe({
      next: (res) => {
        localStorage.setItem('player_session', JSON.stringify(res));
        this.joining.set(false);
        this.router.navigate(['/lobby', res.partitaId]);
      },
      error: (err) => {
        this.joining.set(false);
        if (err.status === 404) {
          this.joinErrorMsg.set('Codice partita non valido.');
        } else if (err.status === 409) {
          this.joinErrorMsg.set(err.error ?? 'Nickname già in uso o partita già avviata.');
        } else {
          this.joinErrorMsg.set('Errore di connessione. Riprova.');
        }
      },
    });
  }

  // --- accesso game master ---
  showAdminModal = signal(false);
  adminUsername = signal('');
  adminPassword = signal('');
  adminError = signal(false);
  adminLoading = signal(false);

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
        this.router.navigate(['/admin']);
      },
      error: () => {
        this.adminLoading.set(false);
        this.adminError.set(true);
      },
    });
  }
}