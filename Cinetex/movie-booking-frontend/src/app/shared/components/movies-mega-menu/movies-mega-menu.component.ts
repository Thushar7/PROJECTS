import { Component, EventEmitter, Output, signal, inject, OnInit, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GenresApiService } from '../../../core/services/genres-api.service';
import { LanguagesApiService } from '../../../core/services/languages-api.service';

@Component({
  selector: 'app-movies-mega-menu',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './movies-mega-menu.component.html',
  styleUrls: ['./movies-mega-menu.component.scss']
})
export class MoviesMegaMenuComponent implements OnInit {
  private genresApi = inject(GenresApiService);
  private languagesApi = inject(LanguagesApiService);

  readonly genres = signal<string[]>([]);
  readonly languages = signal<string[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  private loaded = false;

  @Output() selectGenre = new EventEmitter<string>();
  @Output() selectLanguage = new EventEmitter<string>();

  ngOnInit() {
    // Lazy fetch triggered externally by parent when menu first shown
  }

  ensureLoaded() {
    if (this.loaded) return;
    this.loaded = true;
    this.loading.set(true);
    this.error.set(null);
    // Fetch in parallel
    this.genresApi.getAll().subscribe({
      next: g => this.genres.set(g),
      error: () => this.error.set('Failed to load genres'),
      complete: () => this.loading.set(false)
    });
    this.languagesApi.getAll().subscribe({
      next: l => this.languages.set(l),
      error: () => this.error.set('Failed to load languages')
    });
  }

  onGenre(g: string) { this.selectGenre.emit(g); }
  onLanguage(l: string) { this.selectLanguage.emit(l); }
}
