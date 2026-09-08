import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { GenresApiService, GenreDto } from '../../../core/services/genres-api.service';
import { LanguagesApiService, LanguageDto } from '../../../core/services/languages-api.service';
import { MoviesApiService } from '../../../core/services/movies-api.service';

interface FormState {
  title: string;
  duration: number | null;
  releaseDate: string;
  description: string;
  genreId: number | null;
  languageId: number | null;
  submitting: boolean;
  error: string | null;
}

@Component({
  selector: 'app-add-movie-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-movie-page.component.html',
  styleUrls: ['./add-movie-page.component.scss']
})
export class AddMoviePageComponent {
  private readonly genresApi = inject(GenresApiService);
  private readonly languagesApi = inject(LanguagesApiService);
  private readonly moviesApi = inject(MoviesApiService);
  private readonly router = inject(Router);

  protected readonly genres = signal<GenreDto[]>([]);
  protected readonly languages = signal<LanguageDto[]>([]);
  protected readonly form = signal<FormState>({
    title: '',
    duration: null,
    releaseDate: '',
    description: '',
    genreId: null,
    languageId: null,
    submitting: false,
    error: null
  });

  protected readonly createdMovieId = signal<string | null>(null);

  constructor() {
    this.loadFacets();
  }

  private loadFacets() {
    this.genresApi.getAllRaw().subscribe({
      next: rows => this.genres.set(rows),
      error: err => console.warn('Failed to load genres', err)
    });
    this.languagesApi.getAllRaw().subscribe({
      next: rows => this.languages.set(rows),
      error: err => console.warn('Failed to load languages', err)
    });
  }

  update<K extends keyof FormState>(key: K, value: FormState[K]) {
    this.form.update(f => ({ ...f, [key]: value }));
  }

  canSubmit(): boolean {
    const f = this.form();
    return !!(f.title && f.duration && f.duration > 0 && f.releaseDate && f.description && f.genreId && f.languageId && !f.submitting);
  }

  submit() {
    if (!this.canSubmit()) return;
    this.form.update(f => ({ ...f, submitting: true, error: null }));
    const f = this.form();
    this.moviesApi.createMovie({
      title: f.title.trim(),
      duration: f.duration!,
      releaseDate: f.releaseDate,
      description: f.description.trim(),
      genreIds: [f.genreId!],
      languageIds: [f.languageId!]
    }).subscribe({
      next: res => {
        this.createdMovieId.set(res.movieId);
        // Redirect to poster step
        this.router.navigate(['/admin/movies', res.movieId, 'poster']);
      },
      error: err => {
        console.error('Create movie failed', err);
        this.form.update(f2 => ({ ...f2, submitting: false, error: 'Failed to create movie' }));
      }
    });
  }
}
