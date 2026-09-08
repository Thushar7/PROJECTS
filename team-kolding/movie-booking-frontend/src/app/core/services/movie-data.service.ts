import { Injectable, computed, effect, inject, signal } from '@angular/core';
import { Movie, MovieCategory } from '../models/movie.model';
import { MoviesApiService } from './movies-api.service';
import { GenresApiService } from './genres-api.service';

@Injectable({ providedIn: 'root' })
export class MovieDataService {
  private readonly moviesApi = inject(MoviesApiService);
  private readonly genresApi = inject(GenresApiService);

  // Dummy hero/banner movies retained (not part of the backend-driven categories).
  private readonly heroMoviesSignal = signal<Movie[]>([
    {
      id: 'tt0111161',
      title: 'The Shawshank Redemption',
      genre: ['Drama'],
      posterUrl: 'https://image.tmdb.org/t/p/w342/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg',
      bannerUrl: 'https://image.tmdb.org/t/p/original/iNh3BivHyg5sQRPP1KOkzguEX0H.jpg',
      rating: 9.3,
      year: 1994,
      durationMinutes: 142,
      description: 'Two imprisoned men bond over a number of years, finding solace and eventual redemption.',
      language: 'English'
    },
  ]);
  readonly heroMovies = this.heroMoviesSignal.asReadonly();
  getHeroMovie(): Movie | undefined { return this.heroMovies()[0]; }

  // Target genre names to display (replacing previous dummy categories)
  private readonly targetGenres = ['Action', 'Drama', 'Sci-Fi'] as const;

  // Runtime mapping name -> id (loaded once via GenresApiService)
  private readonly genreNameToId = new Map<string, number>();
  private readonly genresLoaded = signal(false);
  private readonly genresError = signal<string | null>(null);

  // Individual category movie lists + loading/error states
  private readonly actionMovies = signal<Movie[]>([]);
  private readonly dramaMovies = signal<Movie[]>([]);
  private readonly scifiMovies = signal<Movie[]>([]);

  private readonly loadingStates = signal<Record<string, boolean>>({});
  private readonly errorStates = signal<Record<string, string | null>>({});

  // Load genre IDs once
  private readonly loadGenresEffect = effect(onCleanup => {
    if (this.genresLoaded()) return;
    const sub = this.genresApi.getAllRaw().subscribe({
      next: rows => {
        rows.forEach(r => this.genreNameToId.set(r.name.toLowerCase(), r.genre_id));
        this.genresLoaded.set(true);
        this.fetchAllTargetGenres();
      },
      error: err => {
        this.genresError.set(err?.message || 'Failed to load genres');
        this.genresLoaded.set(true); // still mark to avoid endless retry loop
      }
    });
    onCleanup(() => sub.unsubscribe());
  });

  private fetchAllTargetGenres() {
    for (const g of this.targetGenres) this.fetchGenreMovies(g);
  }

  private updateLoading(key: string, loading: boolean) {
    this.loadingStates.update(state => ({ ...state, [key]: loading }));
  }
  private updateError(key: string, error: string | null) {
    this.errorStates.update(state => ({ ...state, [key]: error }));
  }

  private fetchGenreMovies(genreName: string) {
    const key = genreName.toLowerCase();
    const genreId = this.genreNameToId.get(key);
    if (genreId == null) {
      // If we don't have an id yet (genres not loaded), wait until genresLoaded triggers again.
      if (!this.genresLoaded()) return;
      console.warn('[MovieDataService] No genre id found for', genreName);
      return;
    }
    this.updateLoading(key, true);
    this.updateError(key, null);
    const sub = this.moviesApi.query({ genreIds: [genreId], page: 0 }).subscribe({
      next: list => {
        switch (genreName) {
          case 'Action': this.actionMovies.set(list); break;
          case 'Drama': this.dramaMovies.set(list); break;
          case 'Sci-Fi': this.scifiMovies.set(list); break;
        }
        this.updateLoading(key, false);
      },
      error: err => {
        this.updateError(key, err?.message || 'Failed to load movies');
        this.updateLoading(key, false);
      }
    });
    // Fire-and-forget; GC when service destroyed (singleton for app lifetime). If memory concerns, store subs & clean up.
  }

  readonly categories = computed<MovieCategory[]>(() => [
    { key: 'action', label: 'Action', movies: this.actionMovies() },
    { key: 'drama', label: 'Drama', movies: this.dramaMovies() },
    { key: 'sci-fi', label: 'Sci-Fi', movies: this.scifiMovies() }
  ]);

  // Flattened list of all loaded movies
  readonly allMovies = computed<Movie[]>(() => this.categories().flatMap(c => c.movies));

  getCategory(key: string): MovieCategory | undefined {
    return this.categories().find(c => c.key === key);
  }

  // Expose loading/error helpers for UI (not yet wired in home page but available)
  isLoadingCategory(key: string): boolean {
    return !!this.loadingStates()[key];
  }
  getCategoryError(key: string): string | null | undefined {
    return this.errorStates()[key];
  }
}