import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ShowTimeDto {
  showtimeId: number;
  showDate: string;          // e.g. 2025-10-06
  startTime: string;         // ISO string
  endTime: string;           // ISO string
  movieId: number;
  theatreId: number;
}

@Injectable({ providedIn: 'root' })
export class ShowtimesApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBase;

  getByMovie(movieId: number): Observable<ShowTimeDto[]> {
    const url = `${this.base}/showtimes?movieId=${movieId}`;
    const started = performance.now();
    console.log('[Showtimes][Request]', url);
    return this.http.get<ShowTimeDto[]>(`${this.base}/showtimes`, { params: { movieId } as any }).pipe(
      map(list => Array.isArray(list) ? list : []),
      map(list => {
        const elapsed = (performance.now() - started).toFixed(1);
        console.log('[Showtimes][Response]', { url, count: list.length, elapsedMs: elapsed });
        return list;
      })
    );
  }

  /** Fetch a single showtime by id */
  getById(showtimeId: number | string): Observable<ShowTimeDto | null> {
    const url = `${this.base}/showtimes/${showtimeId}`;
    console.log('[Showtimes][Request:getById]', url);
    return this.http.get<ShowTimeDto | null>(url).pipe(
      map(res => res ?? null)
    );
  }

  /** Create a new showtime */
  createShowtime(payload: { movieId: number; theatreId: number; startTime: string; endTime: string; showDate: string; }): Observable<ShowTimeDto> {
    const url = `${this.base}/showtimes`;
    console.log('[Showtimes][Request:POST]', url, payload);
    const started = performance.now();
    return this.http.post<any>(url, payload).pipe(
      map(res => {
        const dto: ShowTimeDto = {
          showtimeId: res?.showtimeId ?? res?.id ?? 0,
          showDate: res?.showDate ?? payload.showDate,
          startTime: res?.startTime ?? payload.startTime,
          endTime: res?.endTime ?? payload.endTime,
          movieId: res?.movieId ?? payload.movieId,
          theatreId: res?.theatreId ?? payload.theatreId
        };
        const elapsed = (performance.now() - started).toFixed(1);
        console.log('[Showtimes][Response:POST]', { url, dto, elapsedMs: elapsed });
        return dto;
      })
    );
  }
}
