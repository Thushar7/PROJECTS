import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingApiService, BookingDto } from '../../../core/services/booking-api.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-admin-bookings-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-bookings-list.component.html',
  styleUrl: './admin-bookings-list.component.scss'
})
export class AdminBookingsListComponent {
  private readonly http = inject(HttpClient); // Raw HTTP for simplistic list endpoint assumption
  private readonly base = environment.apiBase;
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly bookings = signal<BookingDto[]>([]);

  constructor() { this.load(); }

  load() {
    this.loading.set(true); this.error.set(null);
    // Assuming GET /bookings returns list
    this.http.get<BookingDto[]>(`${this.base}/bookings/get-all`).subscribe({
      next: list => { this.bookings.set(Array.isArray(list) ? list : []); this.loading.set(false); },
      error: err => { this.error.set(err?.error?.message || 'Failed to load bookings'); this.loading.set(false); }
    });
  }
}
