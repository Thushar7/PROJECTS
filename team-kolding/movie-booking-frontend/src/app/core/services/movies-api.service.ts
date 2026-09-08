import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Movie } from '../models/movie.model';
import { Observable, map } from 'rxjs';
import { MOVIES_API_BASE_URL, MOVIES_FILTER_API_BASE_URL } from '../tokens/api-base-urls.token';

@Injectable({ providedIn: 'root' })
export class MoviesApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrlAll = inject(MOVIES_API_BASE_URL);
  private readonly baseUrlFilter = inject(MOVIES_FILTER_API_BASE_URL);

  // Raw backend shape (adjust optional fields as your API evolves)
  // Example current DB columns: movie_id, title, duration, release_date, description
  private mapMovie(raw: any): Movie {
    // Normalize potential field names
    const id = String(raw.movieId ?? raw.id ?? crypto.randomUUID());
    const title = raw.title ?? 'Untitled';
    const description = raw.description ?? 'No description available.';
    const durationMinutes = typeof raw.duration === 'number' ? raw.duration : parseInt(raw.duration, 10) || 0;
    // release_date may be '2024-09-01' -> derive year
    const releaseDate: string | undefined = raw.release_date || raw.releaseDate;
    let year = new Date().getFullYear();
    if (releaseDate) {
      const maybeYear = parseInt(releaseDate.slice(0, 4), 10);
      if (!isNaN(maybeYear)) year = maybeYear;
    } else if (raw.year && !isNaN(Number(raw.year))) {
      year = Number(raw.year);
    }
    const genresArr: string[] = Array.isArray(raw.genres)
      ? raw.genres.filter(Boolean)
      : typeof raw.genres === 'string'
        ? raw.genres.split(',').map((g: string) => g.trim()).filter(Boolean)
        : [];
    const languagesArr: string[] | undefined = Array.isArray(raw.languages)
      ? raw.languages.filter(Boolean)
      : typeof raw.languages === 'string'
        ? raw.languages.split(',').map((l: string) => l.trim()).filter(Boolean)
        : undefined;
    const _genreIds: number[] = Array.isArray(raw.genreIds) ? raw.genreIds.filter((n: any) => typeof n === 'number') : [];
    const _languageIds: number[] = Array.isArray(raw.languageIds) ? raw.languageIds.filter((n: any) => typeof n === 'number') : [];
    // Poster handling priority:
    // 1. Explicit URL field (posterUrl/poster_url)
    // 2. Inline Base64 payload (posterBase64 + posterContentType) -> convert to data URI
    // 3. Fallback placeholder
    let posterUrl: string | undefined = raw.posterUrl || raw.poster_url;
    if (!posterUrl && raw.posterBase64) {
      const ct = (raw.posterContentType || 'image/jpeg').toString();
      const base64: string = String(raw.posterBase64).trim();
      if (base64.length > 0) {
        posterUrl = `data:${ct};base64,${base64}`;
      }
    }
    if (!posterUrl) {
      posterUrl = '/assets/placeholder-poster.png';
    }
    const bannerUrl = raw.bannerUrl || raw.banner_url || undefined;
    const rating = typeof raw.rating === 'number' ? raw.rating : 0;
  const language = raw.language || (languagesArr && languagesArr[0]) || undefined;
    return {
      id,
      title,
      description,
      durationMinutes,
      year,
  genre: genresArr, // keep legacy for now
  genres: genresArr,
      language,
      languages: languagesArr,
      posterUrl,
      bannerUrl,
      rating,
      releaseDate,
      _genreIds,
      _languageIds
    };
  }

  getAll(): Observable<Movie[]> {
    const url = `${this.baseUrlAll}/movies`;
    console.log('[MoviesApiService] GET (all):', url);
    return this.http.get<any[]>(url).pipe(
      map(list => (Array.isArray(list) ? list.map(r => this.mapMovie(r)) : []))
    );
  }

  /** Fetch a single movie by id */
  getById(id: number | string): Observable<Movie | null> {
    const url = `${this.baseUrlAll}/movies/${id}`;
    console.log('[MoviesApiService] GET (by id):', url);
    return this.http.get<any>(url).pipe(
      map(raw => raw ? this.mapMovie(raw) : null)
    );
  }

  query(params: { q?: string; genreIds?: number[]; languageIds?: number[]; minRating?: number | null; page?: number }): Observable<Movie[]> {
    // When a free-text search (q) is present we switch to dedicated /movies/search endpoint.
    // Backend contract: GET /movies/search?title=<substring>
    const hasSearch = !!params.q;
    if (hasSearch) {
      const sp = new URLSearchParams();
      sp.set('title', params.q!);
      if (params.genreIds && params.genreIds.length) sp.set('genres', params.genreIds.join(','));
      if (params.languageIds && params.languageIds.length) sp.set('languages', params.languageIds.join(','));
      if (params.minRating != null) sp.set('minRating', String(params.minRating));
      if (typeof params.page === 'number') sp.set('page', String(params.page));
      const qs = sp.toString();
      const url = `${this.baseUrlFilter}/movies/search?${qs}`;
      console.log('[MoviesApiService] GET (search query):', url);
      return this.http.get<any[]>(url).pipe(
        map(list => (Array.isArray(list) ? list.map(r => this.mapMovie(r)) : []))
      );
    }
    // Use /movies endpoint for paginated/filtered list
    const sp = new URLSearchParams();
    if (params.genreIds && params.genreIds.length) sp.set('genres', params.genreIds.join(','));
    if (params.languageIds && params.languageIds.length) sp.set('languages', params.languageIds.join(','));
    if (params.minRating != null) sp.set('minRating', String(params.minRating));
    if (typeof params.page === 'number') sp.set('page', String(params.page));
    const qs = sp.toString();
    const url = qs ? `${this.baseUrlFilter}/movies?${qs}` : `${this.baseUrlAll}/movies`;
    console.log('[MoviesApiService] GET (query no-search):', url);
    return this.http.get<any[]>(url).pipe(
      map(list => (Array.isArray(list) ? list.map(r => this.mapMovie(r)) : []))
    );
  }

  /** Convenience method for pure title search (no filters) */
  search(title: string): Observable<Movie[]> {
    if (!title || !title.trim()) return this.getAll();
    const url = `${this.baseUrlFilter}/movies/search?title=${encodeURIComponent(title.trim())}`;
    console.log('[MoviesApiService] GET (search convenience):', url);
    return this.http.get<any[]>(url).pipe(
      map(list => (Array.isArray(list) ? list.map(r => this.mapMovie(r)) : []))
    );
  }

  /**
   * Fetch total number of pages from /movies/page endpoint.
   * Returns 0 on failure to allow graceful UI handling.
   */
  getTotalPages(): Observable<number> {
    const url = `${this.baseUrlAll}/movies/page`;
    console.log('[MoviesApiService] GET (total pages):', url);
    return this.http.get<any>(url).pipe(
      map(resp => {
        if (!resp) return 0;
        // Accept either { totalPages: N } or a plain number
        if (typeof resp === 'number') return resp;
        const maybe = resp.totalPages ?? resp.total_pages ?? resp.pages;
        return typeof maybe === 'number' ? maybe : 0;
      })
    );
  }

  /**
   * Fetch recommended movies by plain text genre + language names.
   * Backend requirement (per request): GET /movies?genre="Actions"&languge="English"
   */
  getRecommendedByNames(genre?: string | null, language?: string | null): Observable<Movie[]> {
    const params: string[] = [];
    if (genre) params.push(`genre="${encodeURIComponent(genre)}"`);
    if (language) params.push(`languge="${encodeURIComponent(language)}"`);
    const qs = params.join('&');
    const url = qs ? `${this.baseUrlAll}/movies?${qs}` : `${this.baseUrlAll}/movies`;
    console.log('[MoviesApiService] GET (recommended by names):', url);
    return this.http.get<any[]>(url).pipe(
      map(list => (Array.isArray(list) ? list.map(r => this.mapMovie(r)) : []))
    );
  }

  /**
   * Create a new movie.
   * Contract: backend returns at least an object containing movieId (or id) plus other fields.
   * We normalize via mapMovie(). If only movieId is returned, we produce a minimal Movie shape.
   */
  createMovie(payload: {
    title: string;
    duration: number; // minutes
    releaseDate: string; // ISO date (YYYY-MM-DD)
    description: string;
    genreIds: number[];
    languageIds: number[];
  }): Observable<{ movieId: string; movie: Movie }> {
    const url = `${this.baseUrlAll}/movies`;
    console.log('[MoviesApiService] POST (create movie):', url, payload);
    return this.http.post<any>(url, payload).pipe(
      map(resp => {
        // Accept shapes: { movieId, ... } OR { id, ... } OR raw id value
        let raw: any;
        if (resp && typeof resp === 'object') {
          raw = resp;
        } else if (typeof resp === 'number' || typeof resp === 'string') {
          raw = { movieId: resp, title: payload.title, duration: payload.duration, releaseDate: payload.releaseDate, description: payload.description, genreIds: payload.genreIds, languageIds: payload.languageIds };
        } else {
          raw = { movieId: crypto.randomUUID(), title: payload.title };
        }
        const id = String(raw.movieId ?? raw.id ?? raw.movie_id ?? raw.movieID);
        // Ensure ids arrays are preserved for subsequent enrichment mapping.
        raw.genreIds = raw.genreIds ?? payload.genreIds;
        raw.languageIds = raw.languageIds ?? payload.languageIds;
        const movie = this.mapMovie(raw);
        return { movieId: id, movie };
      })
    );
  }

  /**
   * Upload poster image for an existing movie.
   * Accepts File (jpg/png). Returns boolean or updated Movie metadata if backend responds so.
   */
  uploadPoster(movieId: string | number, file: File): Observable<boolean> {
    const id = String(movieId);
    const url = `${this.baseUrlAll}/movies/${id}/poster`;
    if (!file || !(file instanceof File)) throw new Error('Poster file is required');
    if (!/image\/(jpeg|jpg|png)/i.test(file.type)) {
      console.warn('[MoviesApiService] Non JPG/PNG provided, attempting anyway. Type:', file.type);
    }
    const form = new FormData();
    form.append('file', file, file.name);
    console.log('[MoviesApiService] POST (upload poster):', url, file.name, file.type, file.size + 'B');
    return this.http.post<any>(url, form).pipe(
      map(resp => {
        // If backend returns success flag or status code translates to success.
        if (resp && typeof resp === 'object') {
          if (resp.success === false) return false;
        }
        return true;
      })
    );
  }

  /**
   * Update existing movie fields.
   * Accepts partial updatable properties; movieId path parameter is authoritative.
   */
  updateMovie(id: string | number, payload: {
    title?: string;
    duration?: number;
    releaseDate?: string;
    description?: string;
    genreIds?: number[];
    languageIds?: number[];
  }): Observable<Movie> {
    const movieId = String(id);
    const url = `${this.baseUrlAll}/movies/${movieId}`;
    console.log('[MoviesApiService] PUT (update movie):', url, payload);
    return this.http.put<any>(url, payload).pipe(
      map(resp => this.mapMovie(resp || { movieId, ...payload }))
    );
  }
}