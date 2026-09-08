import { Injectable, Signal, WritableSignal, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MOVIES_API_BASE_URL } from '../tokens/api-base-urls.token';

interface PosterEntry { url: string; objectUrl: boolean; }

@Injectable({ providedIn: 'root' })
export class PosterImageService {
  private readonly http = inject(HttpClient);
  private readonly base = inject(MOVIES_API_BASE_URL);
  private cache = new Map<string, PosterEntry>();
  private pending = new Map<string, { state: WritableSignal<'idle' | 'loading' | 'loaded' | 'error'>; src: WritableSignal<string | null>; }>();

  getPoster(movieId: string): { src: Signal<string | null>; state: Signal<'idle' | 'loading' | 'loaded' | 'error'> } {
    const id = String(movieId);
    if (!this.pending.has(id)) {
      const record = { state: signal<'idle' | 'loading' | 'loaded' | 'error'>('idle'), src: signal<string | null>(null) };
      this.pending.set(id, record);
      queueMicrotask(() => this.fetchPoster(id));
      return { src: record.src, state: record.state };
    }
    const record = this.pending.get(id)!;
    if (this.cache.has(id) && !record.src()) {
      const entry = this.cache.get(id)!;
      record.src.set(entry.url);
      record.state.set('loaded');
    }
    return { src: record.src, state: record.state };
  }

  private fetchPoster(id: string) {
    const record = this.pending.get(id);
    if (!record) return;
    if (this.cache.has(id)) {
      const entry = this.cache.get(id)!;
      record.src.set(entry.url);
      record.state.set('loaded');
      return;
    }
    record.state.set('loading');
    this.http.get(`${this.base}/movies/${id}/poster`, { responseType: 'blob' }).subscribe({
      next: blob => {
        if (!blob || !(blob instanceof Blob)) {
          record.state.set('error');
          return;
        }
        if (!blob.type.startsWith('image/')) {
            record.state.set('error');
            return;
        }
        const objectUrl = URL.createObjectURL(blob);
        this.cache.set(id, { url: objectUrl, objectUrl: true });
        record.src.set(objectUrl);
        record.state.set('loaded');
      },
      error: () => {
        record.state.set('error');
      }
    });
  }

  revokeAll() {
    for (const [, entry] of this.cache) {
      if (entry.objectUrl) URL.revokeObjectURL(entry.url);
    }
    this.cache.clear();
  }
}