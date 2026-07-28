import { Component, Input, Output, EventEmitter, HostListener } from '@angular/core';

/**
 * Popup di conferma generico, riutilizzabile in tutta l'app ovunque serva
 * chiedere una conferma (non solo nella dashboard del Game Master).
 */
@Component({
  selector: 'app-confirm-dialog',
  imports: [],
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.css',
})
export class ConfirmDialog {
  @Input() title = 'Sei sicuro?';
  @Input() message = '';
  @Input() confirmLabel = 'Conferma';
  @Input() cancelLabel = 'Annulla';
  @Input() danger = false;
  @Input() loading = false;

  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  @HostListener('document:keydown.escape')
  onEscape() {
    if (!this.loading) this.cancel.emit();
  }

  onBackdropClick() {
    if (!this.loading) this.cancel.emit();
  }
}