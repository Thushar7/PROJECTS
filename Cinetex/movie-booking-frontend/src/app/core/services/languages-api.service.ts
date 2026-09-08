import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { MOVIES_FILTER_API_BASE_URL } from '../tokens/api-base-urls.token';

export interface LanguageDto { language_id: number; name: string; }

@Injectable({ providedIn: 'root' })
export class LanguagesApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(MOVIES_FILTER_API_BASE_URL);

  getAllRaw(): Observable<LanguageDto[]> {
    return this.http.get<any>(`${this.baseUrl}/movies/languages`).pipe(
      map(raw => {
        const list = Array.isArray(raw) ? raw : Array.isArray(raw?.data) ? raw.data : [];
        return list
          .map((r: any) => {
            if (!r) return null;
            const id = r.language_id ?? r.id ?? r.languageId;
            const name = r.name ?? r.language_name ?? r.language;
            if (id == null || !name) return null;
            return { language_id: Number(id), name: String(name) } as LanguageDto;
          })
          .filter(Boolean) as LanguageDto[];
      })
    );
  }

  getAll(): Observable<string[]> {
    return this.getAllRaw().pipe(map(list => list.map(l => l.name)));
  }

  /** Fetch single language by id */
  getById(id: number | string): Observable<LanguageDto | null> {
    return this.http.get<any>(`${this.baseUrl}/movies/languages/${id}`).pipe(
      map(r => {
        if (!r) return null;
        const lid = r.language_id ?? r.id ?? r.languageId;
        const name = r.name ?? r.language_name ?? r.language;
        if (lid == null || !name) return null;
        return { language_id: Number(lid), name: String(name) };
      })
    );
  }
}
