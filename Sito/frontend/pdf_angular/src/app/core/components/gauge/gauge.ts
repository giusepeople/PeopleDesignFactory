import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-gauge',
  imports: [],
  templateUrl: './gauge.html',
  styleUrl: './gauge.css',
})
export class Gauge {
  @Input() percent = 0;
  @Input() label = '--:--';
  @Input() size: 'sm' | 'lg' = 'sm';

  get clampedPercent(): number {
    return Math.min(100, Math.max(0, this.percent));
  }

  get needleAngle(): number {
    return -90 + (this.clampedPercent / 100) * 180;
  }

  get zone(): 'green' | 'amber' | 'red' {
    if (this.clampedPercent >= 85) return 'red';
    if (this.clampedPercent >= 60) return 'amber';
    return 'green';
  }
}