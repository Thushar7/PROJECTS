import { Component, Input, Output, EventEmitter, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-payment-success',
  imports: [CommonModule],
  templateUrl: './payment-success.component.html',
  styleUrls: ['./payment-success.component.scss']
})
export class PaymentSuccessComponent {
  @Input() amount: number = 0;
  @Input() method: string | null = null;
  @Input() currencyCode = 'USD';
  @Output() done = new EventEmitter<void>();

  readonly visible = signal(true);

  // Allow manual close (e.g. skip delay) while still emitting once
  close() {
    if (!this.visible()) return;
    this.visible.set(false);
    this.done.emit();
  }
}
