import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, map, of, shareReplay } from 'rxjs';

export interface TheatreDto {
  theatreId: number;
  name: string;
  city?: string;
  state?: string;
}

@Injectable({ providedIn: 'root' })
export class TheatresApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBase; // unified base

  private cache = new Map<number, Observable<TheatreDto | null>>();

  /** List all theatres */
  list(): Observable<TheatreDto[]> {
    return this.http.get<TheatreDto[]>(`${this.base}/theatres`).pipe(
      map(list => Array.isArray(list) ? list : [])
    );
  }

  /** Create a new theatre */
  create(data: { name: string; city?: string; state?: string }): Observable<TheatreDto> {
    return this.http.post<TheatreDto>(`${this.base}/theatres`, data);
  }

  getById(id: number): Observable<TheatreDto | null> {
    if (this.cache.has(id)) return this.cache.get(id)!;
    const url = `${this.base}/theatres/${id}`;
    const obs = this.http.get<TheatreDto>(url).pipe(
      map(t => t ?? null),
      shareReplay(1)
    );
    this.cache.set(id, obs);
    return obs;
  }
}
