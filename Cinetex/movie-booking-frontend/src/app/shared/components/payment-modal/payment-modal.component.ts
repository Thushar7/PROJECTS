import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

export type PaymentMethod = 'UPI' | 'Credit Card' | 'Debit Card' | 'Internet Banking';

@Component({
  standalone: true,
  selector: 'app-payment-modal',
  imports: [CommonModule],
  templateUrl: './payment-modal.component.html',
  styleUrls: ['./payment-modal.component.scss']
})
export class PaymentModalComponent {
  @Input({ required: true }) amount!: number;
  @Input() currencyCode: string = 'USD';
  @Output() cancel = new EventEmitter<void>();
  @Output() confirmed = new EventEmitter<{ method: PaymentMethod }>();

  // Supported methods list (for reference if needed elsewhere)
  readonly methods: PaymentMethod[] = ['UPI', 'Credit Card', 'Debit Card', 'Internet Banking'];
  readonly selectedMethod = signal<PaymentMethod | null>(null);
  readonly processing = signal(false);

  choose(m: PaymentMethod) { if (!this.processing()) this.selectedMethod.set(m); }

  submit() {
    if (!this.selectedMethod() || this.processing()) return;
    this.processing.set(true);
    // Simulate payment delay
    setTimeout(() => {
      this.processing.set(false);
      this.confirmed.emit({ method: this.selectedMethod()! });
    }, 800);
  }

  // Icons now hard-coded inline in template for maximum reliability (no sanitization / innerHTML issues).
}
