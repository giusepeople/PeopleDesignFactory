import { Component, signal } from '@angular/core';
import { STRUTTURA_SESSIONE, RigaStruttura } from '../../struttura-sessione.data';

@Component({
  selector: 'app-info-panel',
  imports: [],
  templateUrl: './info-panel.html',
  styleUrl: './info-panel.css',
})
export class InfoPanel {
  aperto = signal(false);
  strutturaSessione: RigaStruttura[] = STRUTTURA_SESSIONE;

  apri() {
    this.aperto.set(true);
  }

  chiudi() {
    this.aperto.set(false);
  }
}