import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_BASE_URL } from '../tokens/api-base-urls.token';
import { Observable, map } from 'rxjs';

export interface UserSummary {
  id: number | string;
  username: string;
  email?: string;
  role?: string;
  genrePreference?: string;
  languagePreference?: string;
  createdAt?: string;
}

export interface PagedResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface UpdateUserRequest {
  role?: string; // 'ADMIN' | 'USER'
  genrePreference?: string | null;
  languagePreference?: string | null;
}

@Injectable({ providedIn: 'root' })
export class UsersApiService {
  private readonly http = inject(HttpClient);
  private readonly base = inject(API_BASE_URL);
  // Updated base path per clarified backend route structure
  private readonly adminUsersBase = `${this.base}/user/admin/users`;

  // Lists all users (admin-only) from /user/admin/users
  list(opts: { page?: number; size?: number; sort?: string; role?: string; search?: string }): Observable<PagedResult<UserSummary>> {
    let params = new HttpParams();
    if (opts.page != null) params = params.set('page', String(opts.page));
    if (opts.size != null) params = params.set('size', String(opts.size));
    if (opts.sort) params = params.set('sort', opts.sort); // e.g. 'username,asc'
    if (opts.role) params = params.set('role', opts.role);
    if (opts.search) params = params.set('search', opts.search);
    return this.http.get<any>(`${this.adminUsersBase}`, { params }).pipe(
      map(raw => {
        // Case 1: backend already returns paged shape
        if (raw && typeof raw === 'object' && 'content' in raw) {
          const contentArr = Array.isArray(raw.content) ? raw.content.map(this.mapUser) : [];
          const size = raw.size ?? (opts.size ?? 20);
          const totalElements = raw.totalElements ?? contentArr.length;
          const totalPages = raw.totalPages ?? (size > 0 ? Math.max(1, Math.ceil(totalElements / size)) : 1);
          return {
            content: contentArr,
            page: raw.page ?? (opts.page ?? 0),
            size,
            totalElements,
            totalPages
          } satisfies PagedResult<UserSummary>;
        }
        // Case 2: backend returns a bare array (your provided example)
        if (Array.isArray(raw)) {
          const mapped = raw.map(this.mapUser);
          const size = opts.size ?? mapped.length;
          const totalElements = mapped.length;
            return {
              content: mapped,
              page: opts.page ?? 0,
              size,
              totalElements,
              totalPages: size > 0 ? Math.max(1, Math.ceil(totalElements / size)) : 1
            } satisfies PagedResult<UserSummary>;
        }
        // Fallback: unexpected shape
        return {
          content: [],
          page: 0,
          size: opts.size ?? 20,
          totalElements: 0,
          totalPages: 0
        } satisfies PagedResult<UserSummary>;
      })
    );
  }

  getById(id: number | string): Observable<UserSummary> {
    return this.http.get<any>(`${this.adminUsersBase}/${id}`).pipe(map(this.mapUser));
  }

  patch(id: number | string, body: UpdateUserRequest): Observable<UserSummary> {
    return this.http.patch<any>(`${this.adminUsersBase}/${id}`, body).pipe(map(this.mapUser));
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.adminUsersBase}/${id}`);
  }

  bulkDisable(ids: (number | string)[]): Observable<{ disabled: number[] }> {
    return this.http.post<{ ids: number[] }>(`${this.adminUsersBase}/bulk/disable`, { ids }).pipe(
      map(resp => ({ disabled: Array.isArray(resp.ids) ? resp.ids.map(Number) : [] }))
    );
  }

  private readonly mapUser = (raw: any): UserSummary => {
    if (!raw) return { id: 'unknown', username: 'unknown' };
    return {
      id: raw.id ?? raw.userId ?? raw.user_id ?? 'unknown',
      username: raw.username ?? raw.name ?? 'unknown',
      email: raw.email,
      role: raw.role ?? (Array.isArray(raw.roles) ? raw.roles[0] : undefined),
      genrePreference: raw.genrePreference ?? raw.genre_preference ?? raw.genre,
      languagePreference: raw.languagePreference ?? raw.language_preference ?? raw.language,
      createdAt: raw.createdAt ?? raw.created_at
    };
  };
}

