import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { MOVIES_FILTER_API_BASE_URL } from '../tokens/api-base-urls.token';

export interface GenreDto { genre_id: number; name: string; }

@Injectable({ providedIn: 'root' })
export class GenresApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(MOVIES_FILTER_API_BASE_URL);

  // New endpoint returning structured list with IDs expected: [{ genre_id, name }, ...]
  getAllRaw(): Observable<GenreDto[]> {
    return this.http.get<any>(`${this.baseUrl}/movies/genres`).pipe(
      map(raw => {
        const list = Array.isArray(raw) ? raw : Array.isArray(raw?.data) ? raw.data : [];
        return list
          .map((r: any) => {
            if (!r) return null;
            const id = r.genre_id ?? r.id ?? r.genreId;
            const name = r.name ?? r.genre_name ?? r.genre;
            if (id == null || !name) return null;
            return { genre_id: Number(id), name: String(name) } as GenreDto;
          })
          .filter(Boolean) as GenreDto[];
      })
    );
  }

  // Convenience names only (fallback if UI just needs labels)
  getAll(): Observable<string[]> {
    return this.getAllRaw().pipe(map(list => list.map(g => g.name)));
  }

  /** Fetch single genre by id */
  getById(id: number | string): Observable<GenreDto | null> {
    return this.http.get<any>(`${this.baseUrl}/movies/genres/${id}`).pipe(
      map(r => {
        if (!r) return null;
        const gid = r.genre_id ?? r.id ?? r.genreId;
        const name = r.name ?? r.genre_name ?? r.genre;
        if (gid == null || !name) return null;
        return { genre_id: Number(gid), name: String(name) };
      })
    );
  }
}
