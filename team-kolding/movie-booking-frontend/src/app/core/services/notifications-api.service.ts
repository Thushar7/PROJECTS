import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface NotificationDto {
  id: number;
  userId: number;
  bookingId: number;
  paymentId: number | null;
  event: string; // e.g. MOVIE_ADDED, BOOKING_CONFIRMED
  type: string;  // USER / ADMIN etc
  status: string; // e.g. PROCESSED
  message: string;
  createdAt: string;
  processedAt?: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationsApiService {
  private http = inject(HttpClient);
  private base = environment.apiBase;

  listForUser(userId: number | string): Observable<NotificationDto[]> {
    if (!userId) return new Observable<NotificationDto[]>(sub => { sub.next([]); sub.complete(); });
    const url = `${this.base}/notifications/user/${userId}`;
    return this.http.get<any[]>(url).pipe(
      map(arr => (Array.isArray(arr) ? arr : []).map(raw => this.map(raw)))
    );
  }

  private map(raw: any): NotificationDto {
    return {
      id: Number(raw.id ?? 0),
      userId: Number(raw.userId ?? 0),
      bookingId: Number(raw.bookingId ?? 0),
      paymentId: raw.paymentId == null ? null : Number(raw.paymentId),
      event: String(raw.event || ''),
      type: String(raw.type || ''),
      status: String(raw.status || ''),
      message: String(raw.message || ''),
      createdAt: String(raw.createdAt || ''),
      processedAt: raw.processedAt
    };
  }
}