import { Component, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private auth = inject(AuthService);
  private router = inject(Router);
 
  // --- accesso giocatore ---
  pin = signal('');
  pinError = signal(false);
 
  onPinInput(event: Event) {
    const value = (event.target as HTMLInputElement).value
      .toUpperCase()
      .replace(/[^A-Z0-9]/g, '')
      .slice(0, 6);
    this.pin.set(value);
    this.pinError.set(false);
  }
 
  joinGame() {
    if (this.pin().length < 4) {
      this.pinError.set(true);
      return;
    }
    // TODO: POST /games/join { code: this.pin() } -> naviga alla lobby
    console.log('Entra con codice', this.pin());
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
