import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface BookingCreateRequest {
  userId: number;
  movieId: number;
  theatreId: number;
  showtimeId: number;
  totalAmount: number; // cents or currency units depending on backend
  paymentId: number;   // placeholder until payment integration
  bookingStatus: string; // e.g. PENDING
  seatCount: number;
  seatNumbers: string[]; // e.g. ["A1","A2"]
}

export interface BookingDto {
  bookingId: number;
  userId: number;
  movieId: number;
  theatreId: number;
  showtimeId: number | string;
  seatCount: number;
  totalAmount: number;
  bookingStatus: string;
  createdAt?: string;
  seatNumbers: string[];
}

// Future enrichment shape (movie title, theatre name, showtime start) – kept minimal for now
export interface BookingEnriched extends BookingDto {
  movieTitle?: string;
  theatreName?: string;
  showtimeStart?: string;
}

@Injectable({ providedIn: 'root' })
export class BookingApiService {
  private http = inject(HttpClient);
  // Booking service base: unified apiBase
  private base = environment.apiBase;

  /** Fetch current reserved seats for a showtime */
  getReservedSeats(showtimeId: number | string): Observable<string[]> {
    const url = `${this.base}/bookings/showtimes/${showtimeId}/reserved-seats`;
    console.log('[BookingApi] GET reserved seats:', url);
    return this.http.get<string[]>(url).pipe(
      map(list => Array.isArray(list) ? list.filter(v => typeof v === 'string') : []),
      catchError(err => {
        console.warn('[BookingApi] Failed to fetch reserved seats', err);
        return throwError(() => err);
      })
    );
  }

  /** Create booking. Returns 409 error shape if seat race condition occurs */
  createBooking(payload: BookingCreateRequest): Observable<BookingDto> {
    const url = `${this.base}/bookings/create`;
    console.log('[BookingApi] POST create booking', { url, payload });
    return this.http.post<BookingDto>(url, payload).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 409) {
          const msg = err.error?.error || 'Selected seats unavailable. Please retry.';
          return throwError(() => ({ conflict: true, message: msg, raw: err }));
        }
        return throwError(() => err);
      })
    );
  }

  /** List bookings for a specific user */
  listUserBookings(userId: number | string): Observable<BookingDto[]> {
    const url = `${this.base}/bookings/get-all/${userId}`;
    console.log('[BookingApi] GET user bookings', url);
    return this.http.get<any>(url).pipe(
      map(res => {
        // Accept either array or { items: [] }
        const list: any[] = Array.isArray(res) ? res : (Array.isArray(res?.items) ? res.items : []);
        return list.map(raw => this.mapBooking(raw));
      }),
      catchError(err => {
        console.warn('[BookingApi] Failed to fetch user bookings', err);
        return throwError(() => err);
      })
    );
  }

  /** Cancel a booking (idempotent). Backend requires userId as query parameter */
  cancelBooking(bookingId: number | string, userId: number | string): Observable<BookingDto> {
    const url = `${this.base}/bookings/${bookingId}/cancel?userId=${encodeURIComponent(String(userId))}`;
    console.log('[BookingApi] PUT cancel booking', { url, bookingId, userId });
    return this.http.put<any>(url, {}).pipe(
      map(res => this.mapBooking(res)),
      catchError(err => {
        console.warn('[BookingApi] Failed to cancel booking', err);
        return throwError(() => err);
      })
    );
  }

  private mapBooking(raw: any): BookingDto {
    if (!raw || typeof raw !== 'object') {
      return {
        bookingId: 0,
        userId: 0,
        movieId: 0,
        theatreId: 0,
        showtimeId: 0,
        seatCount: 0,
        totalAmount: 0,
        bookingStatus: 'UNKNOWN',
        seatNumbers: []
      };
    }
    return {
      bookingId: Number(raw.bookingId ?? raw.id ?? raw.booking_id ?? 0),
      userId: Number(raw.userId ?? raw.user_id ?? 0),
      movieId: Number(raw.movieId ?? raw.movie_id ?? 0),
      theatreId: Number(raw.theatreId ?? raw.theatre_id ?? 0),
      showtimeId: raw.showtimeId ?? raw.showtime_id ?? 0,
      seatCount: Number(raw.seatCount ?? raw.seat_count ?? raw.seats?.length ?? 0),
      totalAmount: Number(raw.totalAmount ?? raw.total_amount ?? 0),
      bookingStatus: String(raw.bookingStatus ?? raw.status ?? 'UNKNOWN'),
      createdAt: raw.createdAt || raw.created_at,
      seatNumbers: Array.isArray(raw.seatNumbers) ? raw.seatNumbers : (Array.isArray(raw.seats) ? raw.seats : [])
    };
  }
}
