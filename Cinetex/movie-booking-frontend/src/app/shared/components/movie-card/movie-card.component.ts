import { Component, Input, computed, inject, Signal, HostListener, HostBinding } from '@angular/core';
import { Movie } from '../../../core/models/movie.model';
import { DecimalPipe } from '@angular/common';
import { PosterImageService } from '../../../core/services/poster-image.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-movie-card',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './movie-card.component.html',
  styleUrls: ['./movie-card.component.scss']
})
export class MovieCardComponent {
  @Input({ required: true }) movie!: Movie;
  @Input() dense = false;
  @HostBinding('attr.role') hostRole = 'button';
  @HostBinding('style.cursor') hostCursor = 'pointer';

  private readonly posterService = inject(PosterImageService);
  private readonly router = inject(Router);
  protected posterSrc: Signal<string> = computed(() => this.movie?.posterUrl || '/assets/placeholder-poster.png');
  protected posterState: Signal<string> = computed(() => 'loaded');

  ngOnChanges() {
    if (!this.movie) return;
    if (!this.movie.posterUrl) {
      const { src, state } = this.posterService.getPoster(this.movie.id);
      this.posterSrc = computed(() => src() || '/assets/placeholder-poster.png');
      this.posterState = state;
    } else {
      // reset to direct URL
      this.posterSrc = computed(() => this.movie.posterUrl!);
      this.posterState = computed(() => 'loaded');
    }
  }

  navigateToShowtimes() {
    if (!this.movie?.id) {
      console.warn('[MovieCard] No movie id present, cannot navigate');
      return;
    }
    const rawId = this.movie.id;
    // API expects numeric; attempt parse if string
    const numeric = Number(rawId);
    const idForRoute = Number.isNaN(numeric) ? rawId : numeric;
    console.log('[MovieCard] Navigating to showtimes with id:', { rawId, idForRoute });
    this.router.navigate(['/showtimes', idForRoute]);
  }

  @HostListener('click') onClick() { this.navigateToShowtimes(); }
  @HostListener('keydown', ['$event']) onKey(e: KeyboardEvent) {
    if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); this.navigateToShowtimes(); }
  }
}