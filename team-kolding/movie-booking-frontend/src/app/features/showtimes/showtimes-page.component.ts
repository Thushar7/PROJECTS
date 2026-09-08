import { Component, inject, signal, computed, OnDestroy, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ShowtimesApiService, ShowTimeDto } from '../../core/services/showtimes-api.service';
import { MoviesApiService } from '../../core/services/movies-api.service';
import { Movie } from '../../core/models/movie.model';
import { TheatresApiService, TheatreDto } from '../../core/services/theatres-api.service';
import { AuthService } from '../../core/services/auth.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastService } from '../../core/services/toast.service';

@Component({
  standalone: true,
  selector: 'app-showtimes-page',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './showtimes-page.component.html',
  styleUrls: ['./showtimes-page.component.scss']
})
export class ShowtimesPageComponent implements OnDestroy {
  private route = inject(ActivatedRoute);
  private api = inject(ShowtimesApiService);
  private moviesApi = inject(MoviesApiService);
  private theatresApi = inject(TheatresApiService);
  private auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);

  readonly movieId = signal<number | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly showtimes = signal<ShowTimeDto[]>([]);
  readonly movie = signal<Movie | null>(null);
  readonly theatres = signal<Record<number, TheatreDto>>({});
  readonly theatreOptions = signal<TheatreDto[]>([]);
  // Filter state
  readonly selectedCity = signal<string | null>(null);
  readonly selectedState = signal<string | null>(null);

  // Derive theatre list & distinct city/state sets
  private readonly theatreList = computed(() => Object.values(this.theatres()));
  readonly cities = computed(() => {
    const st = this.selectedState();
    const list = this.theatreList();
    const filtered = st ? list.filter(t => t.state === st) : [];
    return Array.from(new Set(filtered.map(t => t.city).filter(Boolean) as string[])).sort();
  });
  readonly states = computed(() => Array.from(new Set(this.theatreList().map(t => t.state).filter(Boolean) as string[])).sort());

  // Filter showtimes before grouping
  private readonly filteredShowtimes = computed(() => {
    const city = this.selectedCity();
    const state = this.selectedState();
    if (!city && !state) return this.showtimes();
    return this.showtimes().filter(st => {
      const th = this.theatres()[st.theatreId];
      if (!th) return false; // until theatre resolves
      if (city && th.city !== city) return false;
      if (state && th.state !== state) return false;
      return true;
    });
  });

  // Group showtimes by date for nicer presentation
  readonly groupedByDate = computed(() => {
    const groups: { date: string; items: ShowTimeDto[] }[] = [];
    const byDate: Record<string, ShowTimeDto[]> = {};
    for (const s of this.filteredShowtimes()) {
      byDate[s.showDate] = byDate[s.showDate] || [];
      byDate[s.showDate].push(s);
    }
    Object.keys(byDate).sort().forEach(date => {
      groups.push({ date, items: byDate[date].sort((a,b) => a.startTime.localeCompare(b.startTime)) });
    });
    return groups;
  });

  private sub = this.route.paramMap.subscribe(pm => {
    const raw = pm.get('movieId');
    console.log('[ShowtimesPage] Route param movieId:', raw);
    if (!raw) { this.error.set('Missing movie id'); return; }
    const num = Number(raw);
    if (Number.isNaN(num)) {
      // allow string fallback but API expects numeric; show error
      this.error.set('Invalid movie id');
      return;
    }
    if (this.movieId() === num) return; // avoid refetch if same
    this.movieId.set(num);
    console.log('[ShowtimesPage] Fetching movie + showtimes for movie', num);
    this.fetch(num);
  });

  ngOnDestroy(): void { this.sub.unsubscribe(); }

  // Effect: when state changes and current city is not in available cities, reset city
  private _resetCityOnStateChange = effect(() => {
    const st = this.selectedState();
    const available = this.cities();
    const currentCity = this.selectedCity();
    if (!st) {
      if (currentCity) this.selectedCity.set(null);
      return;
    }
    if (currentCity && !available.includes(currentCity)) {
      this.selectedCity.set(null);
    }
  });

  private fetch(id: number) {
    this.loading.set(true); this.error.set(null); this.showtimes.set([]); this.movie.set(null);
    this.theatres.set({});
    // Fetch movie and showtimes in parallel (simple approach)
    this.moviesApi.getById(id).subscribe({
      next: m => this.movie.set(m),
      error: err => console.warn('[ShowtimesPage] Failed to load movie details', err)
    });
    this.api.getByMovie(id).subscribe({
      next: list => {
        this.showtimes.set(list);
        this.loading.set(false);
        this.fetchTheatresFor(list);
      },
      error: err => {
        const raw = err?.error?.message || '';
        console.error('[ShowtimesPage] Failed to load showtimes', err);
        this.error.set('Unable to load showtimes right now.');
        // Friendly toast for user
        this.toast.error('Something broke down while fetching showtimes. Our team is on it.');
        this.loading.set(false);
      }
    });
  }

  private fetchTheatresFor(list: ShowTimeDto[]) {
    const unique = Array.from(new Set(list.map(s => s.theatreId))).filter(id => typeof id === 'number');
    for (const tid of unique) {
      this.theatresApi.getById(tid).subscribe({
        next: t => {
          if (!t) return;
          const current = this.theatres();
            if (current[tid]) return; // already set
          this.theatres.set({ ...current, [tid]: t });
        },
        error: err => console.warn('[ShowtimesPage] Failed to load theatre', tid, err)
      });
    }
  }

  trackById = (_: number, s: ShowTimeDto) => s.showtimeId;

  selectShowtime(st: ShowTimeDto) {
    this.router.navigate(['/booking', st.showtimeId]);
  }

  theatreLabel(theatreId: number): string {
    const t = this.theatres()[theatreId];
    if (!t) return `Theatre #${theatreId}`;
    const loc = [t.city, t.state].filter(Boolean).join(', ');
    return loc ? `${t.name} (${loc})` : t.name;
  }

  totalVisibleShowtimes(): number {
    return this.groupedByDate().reduce((sum, g) => sum + g.items.length, 0);
  }

  clearFilters() {
    this.selectedCity.set(null);
    this.selectedState.set(null);
  }

  formatTime(dateStr: string) {
    // Accept either full ISO (with Z or offset) or naive local 'YYYY-MM-DDTHH:mm:ss'
    try {
      // If string has no timezone designator but matches date-time pattern, treat as local.
      if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}/.test(dateStr) && !/[zZ]|[+-]\d{2}:?\d{2}$/.test(dateStr)) {
        const [d, t] = dateStr.split('T');
        const [hh, mm] = t.split(':');
        return `${hh}:${mm}`;
      }
      return new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch { return dateStr; }
  }

  // ---------- Admin Add Showtime Modal State ----------
  readonly canAddShowtime = computed(() => this.auth.hasRole('admin') && this.movieId() != null);
  readonly adding = signal(false);
  readonly addSubmitting = signal(false);
  readonly addError = signal<string | null>(null);
  readonly formTheatreId = signal<number | null>(null);
  readonly formShowDate = signal(''); // YYYY-MM-DD
  readonly formStartTime = signal(''); // HH:MM (local)
  readonly formEndTime = signal(''); // HH:MM (local)
  readonly formEndDate = signal(''); // Optional YYYY-MM-DD (if crosses midnight)

  openAddShowtime() {
    if (!this.canAddShowtime()) return;
    this.addError.set(null);
    this.adding.set(true);
    // Load theatre list if empty (basic list call to theatres service expected existing method list())
    if (!this.theatreOptions().length) {
      this.theatresApi.list().subscribe({
        next: rows => this.theatreOptions.set(rows || []),
        error: err => console.warn('[ShowtimesPage] Failed to load theatre options', err)
      });
    }
  }

  closeAddShowtime() {
    if (this.addSubmitting()) return;
    this.adding.set(false);
  }

  canSubmitNew(): boolean {
    // End date optional; if omitted and end time < start time we infer next day.
    return !!(this.formTheatreId() && this.formShowDate() && this.formStartTime() && this.formEndTime() && !this.addSubmitting());
  }

  submitNewShowtime() {
    if (!this.canSubmitNew() || this.movieId() == null) return;
    this.addSubmitting.set(true); this.addError.set(null);
    // Compose local date-time strings WITHOUT converting to UTC to avoid shifting times.
    // Determine end date: provided explicitly or inferred (next day if end < start)
    const startDate = this.formShowDate();
    let endDate = this.formEndDate();
    const startTime = this.formStartTime();
    const endTime = this.formEndTime();
    if (!endDate) {
      // Infer if end time is "earlier" than start time -> crosses midnight
      if (startTime && endTime && endTime < startTime) {
        const d = new Date(`${startDate}T00:00:00`);
        d.setDate(d.getDate() + 1);
        endDate = d.toISOString().slice(0,10); // YYYY-MM-DD
      } else {
        endDate = startDate;
      }
    }
    const startIso = `${startDate}T${startTime}:00`;
    const endIso = `${endDate}T${endTime}:00`;
    this.api.createShowtime({
      movieId: this.movieId()!,
      theatreId: this.formTheatreId()!,
      showDate: startDate, // keep primary showDate as the start date
      startTime: startIso,
      endTime: endIso
    }).subscribe({
      next: created => {
        // Append & regroup
        this.showtimes.update(list => [...list, created]);
        this.addSubmitting.set(false);
        this.adding.set(false);
      },
      error: err => {
        console.error('[ShowtimesPage] Failed to create showtime', err);
        this.addError.set('Failed to create showtime');
        this.addSubmitting.set(false);
      }
    });
  }
}
