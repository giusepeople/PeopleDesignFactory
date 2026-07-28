import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, HostListener, signal } from '@angular/core';
import { toDataURL } from 'qrcode';

/**
 * Overlay a schermo intero pensato per essere proiettato in aula: codice
 * partita in grande + QR code che porta dritti alla Home con il PIN già
 * precompilato (vedi Home#ngOnInit, legge il query param "pin").
 */
@Component({
  selector: 'app-game-code-overlay',
  imports: [],
  templateUrl: './game-code-overlay.html',
  styleUrl: './game-code-overlay.css',
})
export class GameCodeOverlay implements OnInit, OnDestroy {
  @Input({ required: true }) codice!: string;
  @Output() close = new EventEmitter<void>();

  qrDataUrl = signal<string | null>(null);
  isFullscreen = signal(false);

  get joinUrl(): string {
    return `${window.location.origin}/?pin=${this.codice}`;
  }

  ngOnInit() {
    toDataURL(this.joinUrl, {
      width: 320,
      margin: 1,
      color: { dark: '#14183A', light: '#FFFFFFFF' },
    })
      .then((url) => this.qrDataUrl.set(url))
      .catch(() => this.qrDataUrl.set(null));

    document.addEventListener('fullscreenchange', this.onFullscreenChange);
  }

  ngOnDestroy() {
    document.removeEventListener('fullscreenchange', this.onFullscreenChange);
    if (document.fullscreenElement) {
      document.exitFullscreen().catch(() => {});
    }
  }

  private onFullscreenChange = () => {
    this.isFullscreen.set(!!document.fullscreenElement);
  };

  @HostListener('document:keydown.escape')
  onEscape() {
    this.close.emit();
  }

  toggleFullscreen() {
    if (document.fullscreenElement) {
      document.exitFullscreen().catch(() => {});
    } else {
      document.documentElement.requestFullscreen().catch(() => {});
    }
  }
}