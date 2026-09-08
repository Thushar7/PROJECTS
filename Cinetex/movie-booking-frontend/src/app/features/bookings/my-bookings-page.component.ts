import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingApiService, BookingDto } from '../../core/services/booking-api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  selector: 'app-my-bookings-page',
  imports: [CommonModule],
  templateUrl: './my-bookings-page.component.html',
  styleUrls: ['./my-bookings-page.component.scss']
})
export class MyBookingsPageComponent implements OnInit {
  private bookingApi = inject(BookingApiService);
  protected auth = inject(AuthService);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly bookings = signal<BookingDto[]>([]);
  readonly cancellingIds = signal<Set<number>>(new Set());

  readonly hasBookings = computed(() => this.bookings().length > 0);

  ngOnInit(): void {
    const profile = this.auth.profile();
    if (!profile) {
      // profile might not be loaded yet; poll once after small delay
      setTimeout(() => this.load(), 250);
    } else {
      this.load();
    }
  }

  private load() {
    const profile = this.auth.profile();
    if (!profile?.id) { return; }
    this.loading.set(true); this.error.set(null);
    this.bookingApi.listUserBookings(profile.id).subscribe({
      next: list => { this.bookings.set(list); this.loading.set(false); },
      error: err => { this.error.set(err?.error?.message || 'Failed to load bookings'); this.loading.set(false); }
    });
  }

  cancel(b: BookingDto) {
    if (!b || this.isCancelled(b.bookingStatus)) return;
    this.cancellingIds.update(s => new Set(s).add(b.bookingId));
    const userId = this.auth.profile()?.id ?? 0;
    this.bookingApi.cancelBooking(b.bookingId, userId).subscribe({
      next: updated => {
        this.cancellingIds.update(s => { const n = new Set(s); n.delete(b.bookingId); return n; });
        this.bookings.update(list => list.map(item => item.bookingId === b.bookingId ? updated : item));
      },
      error: err => {
        this.cancellingIds.update(s => { const n = new Set(s); n.delete(b.bookingId); return n; });
        alert(err?.error?.message || 'Failed to cancel booking');
      }
    });
  }

  isCancelled(status: string | null | undefined): boolean {
    if (!status) return false;
    const s = status.toUpperCase();
    return s === 'CANCELLED' || s === 'CANCELED';
  }

  statusClass(status: string) {
    const s = status.toUpperCase();
    return {
      confirmed: s === 'CONFIRMED',
      cancelled: s === 'CANCELLED' || s === 'CANCELED'
    };
  }

  trackBooking = (_: number, b: BookingDto) => b.bookingId;
}
