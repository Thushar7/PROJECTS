import { Component, EventEmitter, Input, Output, effect, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingApiService, BookingCreateRequest, BookingDto } from '../../../core/services/booking-api.service';
import { generateSeatLayout, selectedSeatIds, toggleSeat, Seat } from './seat-layout.util';
import { AuthService } from '../../../core/services/auth.service';
import { PaymentModalComponent, PaymentMethod } from '../payment-modal/payment-modal.component';
import { PaymentSuccessComponent } from '../payment-success/payment-success.component';

@Component({
  standalone: true,
  selector: 'app-seat-selection',
  imports: [CommonModule, PaymentModalComponent, PaymentSuccessComponent],
  templateUrl: './seat-selection.component.html',
  styleUrls: ['./seat-selection.component.scss']
})
export class SeatSelectionComponent {
  private bookingApi = inject(BookingApiService);
  private auth = inject(AuthService);

  @Input({ required: true }) showtimeId!: number;
  @Input({ required: true }) theatreId!: number;
  @Input({ required: true }) movieId!: number;
  @Input() maxSelectable = 6; // business rule / assumption
  @Input() pricePerSeat = 500; // currency units assumption

  @Output() bookingCreated = new EventEmitter<BookingDto>();
  @Output() cancelled = new EventEmitter<void>();

  readonly loading = signal(false);
  readonly conflictMessage = signal<string | null>(null);
  readonly reservedSeats = signal<string[]>([]);
  readonly layout = signal<Seat[]>([]);
  readonly showPayment = signal(false);
  readonly chosenPaymentMethod = signal<PaymentMethod | null>(null);
  readonly showPaymentSuccess = signal(false);

  readonly totalSelected = computed(() => selectedSeatIds(this.layout()).length);
  // Derive rows grouping without requiring a custom pipe
  readonly rows = computed(() => {
    const by: Record<string, Seat[]> = {};
    for (const s of this.layout()) {
      (by[s.row] = by[s.row] || []).push(s);
    }
    return Object.keys(by).sort().map(row => ({ row, seats: by[row] }));
  });
  readonly totalAmount = computed(() => this.totalSelected() * this.pricePerSeat);

  constructor() {
    effect(() => {
      const st = this.showtimeId; // triggers when input changes
      if (!st) return;
      this.fetchReservedSeats();
    });
  }

  private fetchReservedSeats() {
    if (!this.showtimeId) return;
    this.loading.set(true);
    this.bookingApi.getReservedSeats(this.showtimeId).subscribe({
      next: list => {
        this.reservedSeats.set(list);
        this.layout.set(generateSeatLayout({ reservedSeats: list }));
        this.loading.set(false);
      },
      error: err => {
        // Suppress noisy log for expected 401 when user not logged in; still allow seat selection (unauth view)
        if (err?.status !== 401) {
          console.warn('[SeatSelection] reserved seats fetch failed', err);
        }
        this.layout.set(generateSeatLayout({ reservedSeats: [] }));
        this.loading.set(false);
      }
    });
  }

  toggle(seat: Seat) {
    if (this.loading() || seat.reserved) return;
    this.layout.set(toggleSeat(this.layout(), seat.id, this.maxSelectable));
  }

  openPayment() {
    const seats = selectedSeatIds(this.layout());
    if (!seats.length) return;
    const user = this.auth.user();
    if (!user) {
      alert('Please login to book seats');
      return;
    }
    this.showPayment.set(true);
  }

  onPaymentConfirmed(e: { method: PaymentMethod }) {
    this.chosenPaymentMethod.set(e.method);
    this.showPayment.set(false);
    this.performBooking();
  }

  private performBooking() {
    const seats = selectedSeatIds(this.layout());
    if (!seats.length) return;
    const profile = this.auth.profile();
    const payload: BookingCreateRequest = {
      userId: profile?.id ?? 0,
      movieId: this.movieId,
      theatreId: this.theatreId,
      showtimeId: this.showtimeId,
      totalAmount: this.totalAmount(),
      paymentId: 0,
      bookingStatus: 'CONFIRMED',
      seatCount: seats.length,
      seatNumbers: seats
    };
    this.loading.set(true);
    this.conflictMessage.set(null);
    this.bookingApi.createBooking(payload).subscribe({
      next: dto => {
        this.loading.set(false);
        this.showPaymentSuccess.set(true);
        // emit after brief success animation display
        setTimeout(() => {
          this.showPaymentSuccess.set(false);
          this.bookingCreated.emit(dto);
        }, 1400);
      },
      error: err => {
        this.loading.set(false);
        if (err?.conflict) {
          this.conflictMessage.set(err.message || 'Seat(s) no longer available.');
          this.fetchReservedSeats();
        } else {
          this.conflictMessage.set('Failed to create booking.');
        }
      }
    });
  }

  cancelPayment() { this.showPayment.set(false); }
  successDone() { this.showPaymentSuccess.set(false); }

  cancel() { this.cancelled.emit(); }

  trackSeat = (_: number, s: Seat) => s.id;

  seatClasses(seat: Seat) {
    return {
      reserved: seat.reserved,
      selected: seat.selected
    };
  }

}
