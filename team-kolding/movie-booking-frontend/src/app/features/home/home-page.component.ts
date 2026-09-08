import { Component, computed, inject, signal, effect } from '@angular/core';
import { MovieDataService } from '../../core/services/movie-data.service';
import { HeroBannerComponent } from '../../shared/components/hero-banner/hero-banner.component';
import { MovieCarouselComponent } from '../../shared/components/movie-carousel/movie-carousel.component';
import { FeaturingNowComponent } from '../../shared/components/featuring-now/featuring-now.component';
import { AuthService } from '../../core/services/auth.service';
import { MoviesApiService } from '../../core/services/movies-api.service';
import { GenresApiService } from '../../core/services/genres-api.service';
import { LanguagesApiService } from '../../core/services/languages-api.service';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [HeroBannerComponent, MovieCarouselComponent, FeaturingNowComponent],
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent {
  private readonly movieService = inject(MovieDataService);
  private readonly auth = inject(AuthService);
  private readonly moviesApi = inject(MoviesApiService);
  private readonly genresApi = inject(GenresApiService);
  private readonly languagesApi = inject(LanguagesApiService);
  readonly categories = this.movieService.categories;

  // Use dummy hero movie list retained in MovieDataService for banner (independent of backend categories).
  readonly heroMovie = computed(() => this.movieService.getHeroMovie());

  // Remote recommended movies fetched via API (if backend supports filtering by names via query param mapping).
  private readonly remoteLoading = signal(false);
  private readonly remoteError = signal<string | null>(null);
  private readonly remoteMovies = signal([] as any[]);
  // Internal trigger to force re-evaluation of recommendation fetch logic (e.g., after late profile arrival)
  private readonly _recomputeTrigger = signal(0);

  // Local fallback recommendation (in case remote is empty or preferences not set yet)
  private readonly localFallback = computed(() => {
    const profile = this.auth.profile();
    if (!profile) return [];
    const genrePref = profile.genrePreference?.trim();
    const langPref = profile.languagePreference?.trim();
    if (!genrePref && !langPref) return [];
    const all = this.movieService.allMovies();
    return all.filter(m => {
      const genres = m.genres?.length ? m.genres : m.genre;
      const languages = m.languages?.length ? m.languages : (m.language ? [m.language] : []);
      const genreMatch = genrePref ? genres.map(g => g.toLowerCase()).includes(genrePref.toLowerCase()) : true;
      const langMatch = langPref ? languages.map(l => l.toLowerCase()).includes(langPref.toLowerCase()) : true;
      return genreMatch && langMatch;
    });
  });

  readonly recommendedMovies = computed(() => {
    const remote = this.remoteMovies();
    return remote.length ? remote : this.localFallback();
  });

  readonly showRecommendations = computed(() => this.recommendedMovies().length >= 1 || this.remoteLoading());

  // Effect to fetch remote recommendations when profile preferences available.
  private lastRequestedKey: string | null = null;
  private genreNameToId = new Map<string, number>();
  private languageNameToId = new Map<string, number>();
  private facetLoaded = signal(false);

  // Preload minimal facets once (IDs) for mapping preferences -> IDs
  private readonly preloadFacetsEffect = effect(onCleanup => {
    if (this.facetLoaded()) return; // already loaded
    // Kick off parallel fetches
    const gSub = this.genresApi.getAllRaw().subscribe({
      next: rows => { rows.forEach(r => this.genreNameToId.set(r.name, r.genre_id)); this.checkFacetsReady(); },
      error: () => { this.checkFacetsReady(); }
    });
    const lSub = this.languagesApi.getAllRaw().subscribe({
      next: rows => { rows.forEach(r => this.languageNameToId.set(r.name, r.language_id)); this.checkFacetsReady(); },
      error: () => { this.checkFacetsReady(); }
    });
    onCleanup(() => { gSub.unsubscribe(); lSub.unsubscribe(); });
  });

  private checkFacetsReady() {
    // Mark as ready once at least one fetch returned (best-effort); full accuracy not critical for recommendations
    if (!this.facetLoaded()) this.facetLoaded.set(true);
  }

  private readonly recommendationEffect = effect(onCleanup => {
    // include trigger in dependency graph
    this._recomputeTrigger();
    const profile = this.auth.profile();
    if (!profile) return;
    const genrePref = profile.genrePreference?.trim();
    const langPref = profile.languagePreference?.trim();
    if (!genrePref && !langPref) return;
    if (!this.facetLoaded()) return; // wait for mapping
    const key = `${genrePref ?? ''}__${langPref ?? ''}`;
    if (this.lastRequestedKey === key) return; // avoid duplicate fetch
    this.lastRequestedKey = key;
    const genreId = genrePref ? this.genreNameToId.get(genrePref) : undefined;
    const languageId = langPref ? this.languageNameToId.get(langPref) : undefined;
    const genreIds = genreId != null ? [genreId] : [];
    const languageIds = languageId != null ? [languageId] : [];
    if (!genreIds.length && !languageIds.length) {
      console.log('[Recommended] No ID mapping found for preferences, skipping remote query. Using fallback.');
      return;
    }
    console.log('[Recommended] Query via moviesApi.query with IDs:', { genreIds, languageIds });
    this.remoteLoading.set(true);
    this.remoteError.set(null);
    const started = performance.now();
    const sub = this.moviesApi.query({ genreIds, languageIds, page: 0 }).subscribe({
      next: list => {
        const elapsed = (performance.now() - started).toFixed(1);
        console.log('[Recommended] Response (ID query)', { count: list.length, elapsedMs: elapsed });
        this.remoteMovies.set(list);
        this.remoteLoading.set(false);
      },
      error: err => {
        const elapsed = (performance.now() - started).toFixed(1);
        console.log('[Recommended] Error (ID query)', { error: err, elapsedMs: elapsed });
        this.remoteError.set(err?.message || 'Failed to load recommendations');
        this.remoteLoading.set(false);
      }
    });
    onCleanup(() => sub.unsubscribe());
  });

  // If profile arrives but initial effect did not run (race with facets / mapping) ensure a retry.
  private readonly lateProfileEnsureEffect = effect(() => {
    const profile = this.auth.profile();
    if (!profile) return;
    // If we have preferences, facets are loaded, but still no remote movies and not loading, force another attempt.
    const hasPrefs = !!(profile.genrePreference?.trim() || profile.languagePreference?.trim());
    if (!hasPrefs) return;
    if (!this.facetLoaded()) return; // wait until facet map ready
    if (this.remoteLoading()) return;
    if (this.remoteMovies().length > 0) return;
    // Reset key so recommendationEffect will execute again
    this.lastRequestedKey = null;
    // Increment trigger to re-run recommendationEffect
    this._recomputeTrigger.update(v => v + 1);
  });
}