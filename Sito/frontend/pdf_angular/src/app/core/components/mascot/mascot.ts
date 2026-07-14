import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-mascot',
  imports: [],
  templateUrl: './mascot.html',
  styleUrl: './mascot.css',
})
export class Mascot {
  @Input() size = 120;
  @Input() variant: 'default' | 'ready' = 'default';
}