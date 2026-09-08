import { Component, inject, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { ShowtimesApiService, ShowTimeDto } from '../../core/services/showtimes-api.service';
import { MoviesApiService } from '../../core/services/movies-api.service';
import { PosterImageService } from '../../core/services/poster-image.service';
import { TheatresApiService, TheatreDto } from '../../core/services/theatres-api.service';
import { Movie } from '../../core/models/movie.model';
import { SeatSelectionComponent } from '../../shared/components/seat-selection/seat-selection.component';
import { TicketModalComponent } from '../../shared/components/ticket-modal/ticket-modal.component';
import { BookingDto } from '../../core/services/booking-api.service';

@Component({
  standalone: true,
  selector: 'app-seat-booking-page',
  imports: [CommonModule, RouterModule, SeatSelectionComponent, TicketModalComponent],
  templateUrl: './seat-booking-page.component.html',
  styleUrls: ['./seat-booking-page.component.scss']
})
export class SeatBookingPageComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private showtimesApi = inject(ShowtimesApiService);
  private moviesApi = inject(MoviesApiService);
  private theatresApi = inject(TheatresApiService);
  private posterSvc = inject(PosterImageService);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly showtime = signal<ShowTimeDto | null>(null);
  readonly movie = signal<Movie | null>(null);
  readonly theatre = signal<TheatreDto | null>(null);
  readonly booking = signal<BookingDto | null>(null);
  readonly showTicket = signal(false);
  readonly posterSrc = signal<string | null>(null);
  readonly posterState = signal<'idle' | 'loading' | 'loaded' | 'error'>('idle');
  // internal holder for poster service handle
  private posterHandle: { src: any; state: any } | null = null;

  readonly pageTitle = computed(() => {
    const st = this.showtime();
    const mv = this.movie();
    if (mv && st) return `Book Seats – ${mv.title}`;
    if (mv) return `Book Seats – ${mv.title}`;
    return 'Book Seats';
  });

  constructor() {
    // Establish a single reactive effect to derive poster source with fallback.
    effect(() => {
      const mv = this.movie();
      const handle = this.posterHandle; // may be null initially
      const serviceState = handle ? handle.state() : 'idle';
      const serviceSrc = handle ? handle.src() : null;

      if (serviceSrc) {
        if (this.posterSrc() !== serviceSrc) this.posterSrc.set(serviceSrc);
        if (this.posterState() !== serviceState) this.posterState.set(serviceState as any);
        return;
      }
      // Fallback: use movie.posterUrl if provided & service not yet loaded
      if (mv?.posterUrl && serviceState !== 'loading' && serviceState !== 'loaded') {
        if (this.posterSrc() !== mv.posterUrl) this.posterSrc.set(mv.posterUrl);
        if (this.posterState() !== 'loaded') this.posterState.set('loaded');
      }
    });

    this.route.paramMap.subscribe(pm => {
      const raw = pm.get('showtimeId');
      if (!raw) { this.error.set('Missing showtime id'); this.loading.set(false); return; }
      this.fetch(raw);
    });
  }

  private fetch(showtimeId: string) {
    this.loading.set(true); this.error.set(null);
    this.showtimesApi.getById(showtimeId).subscribe({
      next: st => {
        if (!st) { this.error.set('Showtime not found'); this.loading.set(false); return; }
        this.showtime.set(st);
        // fetch movie & theatre in parallel
        this.moviesApi.getById(st.movieId).subscribe({ next: m => {
            this.movie.set(m);
            if (m?.id) {
              this.posterHandle = this.posterSvc.getPoster(m.id);
            } else {
              this.posterHandle = null;
            }
          }, error: e => { console.warn('Movie fetch failed', e); this.posterHandle = null; } });
        this.theatresApi.getById(st.theatreId).subscribe({ next: t => this.theatre.set(t), error: e => console.warn('Theatre fetch failed', e) });
        this.loading.set(false);
      },
      error: err => { this.error.set(err?.error?.message || 'Failed to load showtime'); this.loading.set(false); }
    });
  }

  formatTime(dateStr: string) {
    try { return new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }); } catch { return dateStr; }
  }

  onBookingCreated(dto: BookingDto) { this.booking.set(dto); this.showTicket.set(true); }
  closeTicket() { this.showTicket.set(false); }
  reset() { this.booking.set(null); this.showTicket.set(false); }
  back() { this.router.navigate(['/']); }
}
