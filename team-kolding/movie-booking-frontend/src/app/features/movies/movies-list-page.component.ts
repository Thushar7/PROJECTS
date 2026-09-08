import { Component, OnInit, inject, signal, effect } from '@angular/core';
import { MoviesApiService } from '../../core/services/movies-api.service';
import { GenresApiService, GenreDto } from '../../core/services/genres-api.service';
import { LanguagesApiService, LanguageDto } from '../../core/services/languages-api.service';
import { MovieFiltersService } from '../../core/services/movie-filters.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MovieCardComponent } from '../../shared/components/movie-card/movie-card.component';
import { Movie } from '../../core/models/movie.model';

@Component({
  selector: 'app-movies-list-page',
  standalone: true,
  imports: [CommonModule, FormsModule, MovieCardComponent],
  templateUrl: './movies-list-page.component.html',
  styleUrls: ['./movies-list-page.component.scss']
})
export class MoviesListPageComponent implements OnInit {
  private readonly api = inject(MoviesApiService);
  protected readonly filters = inject(MovieFiltersService);
  private readonly genresApi = inject(GenresApiService);
  private readonly languagesApi = inject(LanguagesApiService);

  protected readonly search = signal('');
  protected readonly minRating = signal<number | null>(null);

  // Facet lists (from backend tables, fallback to derived when dummy mode)
  protected readonly facetGenres = signal<string[]>([]);
  protected readonly facetLanguages = signal<string[]>([]);
  private readonly genreNameToId = new Map<string, number>();
  private readonly languageNameToId = new Map<string, number>();
  private readonly genreIdToName = new Map<number, string>();
  private readonly languageIdToName = new Map<number, string>();
  private readonly initialServerLoadDone = signal(false);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  private genresFetchDone = false;
  private languagesFetchDone = false;
  private requestCounter = 0; // used to discard stale responses
  private lastFiltersHash = '';
  // Pagination state
  protected readonly currentPage = signal(0); // zero-based
  protected readonly totalPages = signal(0);
  protected readonly pageNumbers = signal<number[]>([]); // [0..totalPages-1]
  private readonly pageSize = 20; // (Assumption) backend default; adjust if endpoint provides
  private totalPagesLoaded = false;
  // NOTE: We fetch total pages once (unfiltered). If future requirement needs total pages to reflect filters
  // we would add a dedicated endpoint call when filters change or include counts in main response.

  // Effect: react to filter changes (must be declared in injection context as a field, not inside ngOnInit)
  private readonly filterEffect = effect(onCleanup => {
    if (!this.initialServerLoadDone()) return; // wait for initial load
  const { search, genres, languages, minRating } = this.filters.getRawFilters();
    const hasActiveFilters = !!(search || genres.length || languages.length || (minRating != null));

    // Hash filters to prevent duplicate identical requests; exclude page so changing page triggers manual fetch
    const hash = JSON.stringify({ search, genres: [...genres].sort(), languages: [...languages].sort(), minRating });
    if (hash === this.lastFiltersHash) return;
    const filtersChanged = this.lastFiltersHash !== '' && hash !== this.lastFiltersHash;
    this.lastFiltersHash = hash;
    if (filtersChanged) {
      // Reset pagination to first page when filters actually change
      this.currentPage.set(0);
    }

    // If no active filters, load current page (unfiltered)
    if (!hasActiveFilters) {
      this.loading.set(true);
      const reqId = ++this.requestCounter;
      const sub = this.api.query({ page: this.currentPage() }).subscribe({
        next: movies => {
          if (reqId !== this.requestCounter) return; // stale
          this.filters.setMovies(this.enrichMoviesWithNames(movies));
          this.loading.set(false);
          this.error.set(null);
        },
        error: err => {
          if (reqId !== this.requestCounter) return;
          console.error('Reload (no filters) failed', err);
          this.loading.set(false);
          this.error.set('Failed to reload movies');
        }
      });
      onCleanup(() => sub.unsubscribe());
      return;
    }

    const genreIds = genres.map(g => this.genreNameToId.get(g)).filter((v): v is number => typeof v === 'number');
    const languageIds = languages.map(l => this.languageNameToId.get(l)).filter((v): v is number => typeof v === 'number');
    this.loading.set(true);
    const reqId = ++this.requestCounter;
    const sub = this.api.query({ q: search || undefined, genreIds, languageIds, minRating, page: this.currentPage() }).subscribe({
      next: movies => {
        if (reqId !== this.requestCounter) return; // discard stale response
  this.filters.setMovies(this.enrichMoviesWithNames(movies));
        this.loading.set(false);
        this.error.set(null);
      },
      error: err => {
        if (reqId !== this.requestCounter) return;
        console.error('Filtered query failed', err);
        this.loading.set(false);
        this.error.set('Failed to load filtered movies');
      }
    });
    onCleanup(() => sub.unsubscribe());
  });

  ngOnInit(): void {
    // Initial load - page 0
    this.loading.set(true);
    this.api.query({ page: 0 }).subscribe({
      next: movies => {
        this.filters.setMovies(this.enrichMoviesWithNames(movies));
        this.initialServerLoadDone.set(true);
        this.loading.set(false);
      },
      error: err => {
        console.error('Initial movies fetch failed', err);
        this.error.set('Failed to load movies');
        this.loading.set(false);
      }
    });

    // Fetch total pages (only once). Assumes endpoint does not depend on filters.
    this.api.getTotalPages().subscribe({
      next: pages => {
        this.totalPages.set(pages);
        this.pageNumbers.set(Array.from({ length: pages }, (_, i) => i));
        this.totalPagesLoaded = true;
      },
      error: err => {
        console.warn('Failed to load total pages', err);
        this.totalPages.set(0);
      }
    });

    // Load structured facets
    this.genresApi.getAllRaw().subscribe({
      next: (rows: GenreDto[]) => {
  rows.forEach(r => { this.genreNameToId.set(r.name, r.genre_id); this.genreIdToName.set(r.genre_id, r.name); });
        this.facetGenres.set(rows.map(r => r.name).sort((a,b)=>a.localeCompare(b)));
        this.genresFetchDone = true;
        this.maybePopulateFallbackFacets();
      },
      error: err => {
        console.warn('Failed to fetch genre list', err);
        this.error.set('Failed to load genres');
        this.genresFetchDone = true;
        this.maybePopulateFallbackFacets();
      }
    });
    this.languagesApi.getAllRaw().subscribe({
      next: (rows: LanguageDto[]) => {
  rows.forEach(r => { this.languageNameToId.set(r.name, r.language_id); this.languageIdToName.set(r.language_id, r.name); });
        this.facetLanguages.set(rows.map(r => r.name).sort((a,b)=>a.localeCompare(b)));
        this.languagesFetchDone = true;
        this.maybePopulateFallbackFacets();
      },
      error: err => {
        console.warn('Failed to fetch language list', err);
        this.error.set('Failed to load languages');
        this.languagesFetchDone = true;
        this.maybePopulateFallbackFacets();
      }
    });

  }

  onSearchChange(value: string) {
    this.filters.patch({ search: value });
  }

  onMinRatingChange(value: string) {
    const num = value ? Number(value) : null;
    this.filters.patch({ minRating: isNaN(Number(num)) ? null : num });
  }

  private maybePopulateFallbackFacets() {
    if (!(this.genresFetchDone && this.languagesFetchDone)) return;
    if (this.facetGenres().length && this.facetLanguages().length) return; // already have data
    // Derive from currently loaded movies as a fallback
    const movies = this.filters.filteredMovies();
    const gset = new Set<string>();
    const lset = new Set<string>();
    for (const m of movies) {
      const g = (m.genres && m.genres.length ? m.genres : m.genre) || [];
      g.forEach(x => x && gset.add(x));
      const langs = (m.languages && m.languages.length ? m.languages : (m.language ? [m.language] : []));
      langs.forEach(x => x && lset.add(x));
    }
    if (!this.facetGenres().length && gset.size) this.facetGenres.set([...gset].sort((a,b)=>a.localeCompare(b)));
    if (!this.facetLanguages().length && lset.size) this.facetLanguages.set([...lset].sort((a,b)=>a.localeCompare(b)));
  }
  private enrichMoviesWithNames(movies: any[]): Movie[] {
    return movies.map(m => {
      const gIds: number[] = (m as any)._genreIds || [];
      if ((!m.genres || !m.genres.length) && gIds.length) {
        const gNames = gIds.map(id => this.genreIdToName.get(id) || String(id));
        m = { ...m, genres: gNames, genre: gNames };
      }
      const lIds: number[] = (m as any)._languageIds || [];
      if ((!m.languages || !m.languages.length) && lIds.length) {
        const lNames = lIds.map(id => this.languageIdToName.get(id) || String(id));
        m = { ...m, languages: lNames, language: lNames[0] };
      }
      return m;
    });
  }

  // Pagination handlers
  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages()) return;
    if (page === this.currentPage()) return;
    this.currentPage.set(page);
    // Trigger new fetch by updating a meaningless part of filter hash via resetting lastFiltersHash
    const { search, genres, languages, minRating } = this.filters.getRawFilters();
    this.lastFiltersHash = ''; // force effect to run
    const genreIds = genres.map(g => this.genreNameToId.get(g)).filter((v): v is number => typeof v === 'number');
    const languageIds = languages.map(l => this.languageNameToId.get(l)).filter((v): v is number => typeof v === 'number');
    this.loading.set(true);
    const reqId = ++this.requestCounter;
    const sub = this.api.query({ q: search || undefined, genreIds, languageIds, minRating, page }).subscribe({
      next: movies => {
        if (reqId !== this.requestCounter) return;
        this.filters.setMovies(this.enrichMoviesWithNames(movies));
        this.loading.set(false);
        this.error.set(null);
      },
      error: err => {
        if (reqId !== this.requestCounter) return;
        console.error('Page fetch failed', err);
        this.loading.set(false);
        this.error.set('Failed to load page');
      }
    });
  }
  nextPage() { this.goToPage(this.currentPage() + 1); }
  prevPage() { this.goToPage(this.currentPage() - 1); }

  /** Fetch current page for unfiltered listing */
  private fetchCurrentPage() {
    this.loading.set(true);
    const reqId = ++this.requestCounter;
    const sub = this.api.query({ page: this.currentPage() }).subscribe({
      next: movies => {
        if (reqId !== this.requestCounter) return;
        this.filters.setMovies(this.enrichMoviesWithNames(movies));
        this.loading.set(false);
        this.error.set(null);
      },
      error: err => {
        if (reqId !== this.requestCounter) return;
        console.error('Unfiltered page fetch failed', err);
        this.loading.set(false);
        this.error.set('Failed to load movies');
      }
    });
  }
}