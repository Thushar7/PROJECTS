import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MoviesApiService } from '../../../core/services/movies-api.service';
import { GenresApiService, GenreDto } from '../../../core/services/genres-api.service';
import { LanguagesApiService, LanguageDto } from '../../../core/services/languages-api.service';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-movies-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './admin-movies-list.component.html',
  styleUrl: './admin-movies-list.component.scss'
})
export class AdminMoviesListComponent {
  private readonly api = inject(MoviesApiService);
  private readonly genresApi = inject(GenresApiService);
  private readonly languagesApi = inject(LanguagesApiService);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly movies = signal<any[]>([]);

  // Edit modal state
  readonly editing = signal(false);
  readonly editLoading = signal(false);
  readonly editSubmitting = signal(false);
  readonly editError = signal<string | null>(null);
  readonly editMovieId = signal<string | null>(null);
  readonly editTitle = signal('');
  readonly editDuration = signal<number | null>(null);
  readonly editReleaseDate = signal('');
  readonly editDescription = signal('');
  readonly editGenreId = signal<number | null>(null);
  readonly editLanguageId = signal<number | null>(null);
  readonly allGenres = signal<GenreDto[]>([]);
  readonly allLanguages = signal<LanguageDto[]>([]);

  openEdit(movieId: string) {
    this.editing.set(true);
    this.editMovieId.set(movieId);
    this.editError.set(null);
    this.editLoading.set(true);
    // parallel fetch: movie details + facets (if not already loaded)
    this.api.getById(movieId).subscribe({
      next: movie => {
        if (!movie) { this.editError.set('Movie not found'); this.editLoading.set(false); return; }
        this.editTitle.set(movie.title || '');
        this.editDuration.set(movie.durationMinutes || null);
        this.editReleaseDate.set(movie.releaseDate || '');
        this.editDescription.set(movie.description || '');
        const gId = (movie as any)._genreIds?.[0];
        const lId = (movie as any)._languageIds?.[0];
        this.editGenreId.set(typeof gId === 'number' ? gId : null);
        this.editLanguageId.set(typeof lId === 'number' ? lId : null);
        this.editLoading.set(false);
      },
      error: err => { this.editError.set('Failed to load movie'); this.editLoading.set(false); }
    });
    if (!this.allGenres().length) {
      this.genresApi.getAllRaw().subscribe({ next: rows => this.allGenres.set(rows) });
    }
    if (!this.allLanguages().length) {
      this.languagesApi.getAllRaw().subscribe({ next: rows => this.allLanguages.set(rows) });
    }
  }

  closeEdit() {
    if (this.editSubmitting()) return; // prevent closing mid-submit
    this.editing.set(false);
    this.editMovieId.set(null);
  }

  canSubmitEdit(): boolean {
    return !!(this.editMovieId() && this.editTitle().trim() && this.editDuration() && this.editDuration()! > 0 && this.editReleaseDate() && this.editDescription().trim() && this.editGenreId() && this.editLanguageId() && !this.editSubmitting());
  }

  submitEdit() {
    if (!this.canSubmitEdit()) return;
    this.editSubmitting.set(true);
    this.editError.set(null);
    const payload: any = {
      title: this.editTitle().trim(),
      duration: this.editDuration()!,
      releaseDate: this.editReleaseDate(),
      description: this.editDescription().trim(),
      genreIds: [this.editGenreId()!],
      languageIds: [this.editLanguageId()!]
    };
    this.api.updateMovie(this.editMovieId()!, payload).subscribe({
      next: updated => {
        // Update list locally
        this.movies.update(list => list.map(m => m.id === updated.id ? { ...m, ...updated } : m));
        this.editSubmitting.set(false);
        this.closeEdit();
      },
      error: err => {
        this.editError.set('Failed to update movie');
        this.editSubmitting.set(false);
      }
    });
  }

  constructor() {
    this.load();
  }

  load() {
    this.loading.set(true); this.error.set(null);
    this.api.getAll().subscribe({
      next: list => { this.movies.set(list); this.loading.set(false); },
      error: err => { this.error.set(err?.error?.message || 'Failed to load movies'); this.loading.set(false); }
    });
  }
}
