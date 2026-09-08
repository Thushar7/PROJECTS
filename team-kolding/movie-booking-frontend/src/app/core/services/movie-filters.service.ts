import { Injectable, Signal, WritableSignal, computed, signal } from '@angular/core';
import { Movie } from '../models/movie.model';

export interface MovieFiltersState {
  search: string;
  genres: string[];
  languages: string[];
  minRating: number | null;
}

@Injectable({ providedIn: 'root' })
export class MovieFiltersService {
  private readonly allMovies: WritableSignal<Movie[]> = signal([]);
  private readonly loading = signal(false);
  private readonly error = signal<string | null>(null);

  private readonly state: WritableSignal<MovieFiltersState> = signal({
    search: '',
    genres: [],
    languages: [],
    minRating: null
  });

  readonly vm = computed(() => ({
    filters: this.state(),
    filtered: this.filteredMovies(),
    total: this.allMovies().length,
    loading: this.loading(),
    error: this.error()
  }));

  readonly filteredMovies: Signal<Movie[]> = computed(() => {
    const { search, genres, languages, minRating } = this.state();
    const s = search.trim().toLowerCase();
    return this.allMovies().filter(m => {
      if (s && !m.title.toLowerCase().includes(s)) return false;
      // Normalize genres (prefer m.genres then m.genre)
      const movieGenres: string[] = (m.genres && m.genres.length ? m.genres : m.genre) || [];
      if (genres.length && !genres.some(g => movieGenres.includes(g))) return false;
      // Normalize languages (prefer m.languages then single m.language)
      const movieLanguages: string[] = (m.languages && m.languages.length ? m.languages : (m.language ? [m.language] : []));
      if (languages.length && !languages.some(l => movieLanguages.includes(l))) return false;
      if (minRating != null && m.rating < minRating) return false;
      return true;
    });
  });

  setMovies(movies: Movie[]) {
    this.allMovies.set(movies);
  }

  patch(partial: Partial<MovieFiltersState>) {
    this.state.update(s => ({ ...s, ...partial }));
  }

  toggleGenre(genre: string) {
    this.state.update(s => ({
      ...s,
      genres: s.genres.includes(genre) ? s.genres.filter(g => g !== genre) : [...s.genres, genre]
    }));
  }

  toggleLanguage(lang: string) {
    this.state.update(s => ({
      ...s,
      languages: s.languages.includes(lang) ? s.languages.filter(l => l !== lang) : [...s.languages, lang]
    }));
  }

  clearFilters() {
    this.state.set({ search: '', genres: [], languages: [], minRating: null });
  }

  // Expose raw filters state for effects without pulling in derived vm()/filtered dependencies
  getRawFilters(): MovieFiltersState {
    return this.state();
  }
}