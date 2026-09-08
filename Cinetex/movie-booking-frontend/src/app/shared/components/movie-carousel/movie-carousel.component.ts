import { Component, ElementRef, Input, ViewChild, computed, signal, AfterViewInit, OnDestroy, OnChanges, SimpleChanges, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { MovieCardComponent } from '../movie-card/movie-card.component';
import { Movie } from '../../../core/models/movie.model';

@Component({
  selector: 'app-movie-carousel',
  standalone: true,
  imports: [MovieCardComponent],
  templateUrl: './movie-carousel.component.html',
  styleUrls: ['./movie-carousel.component.scss']
})
export class MovieCarouselComponent implements AfterViewInit, OnDestroy, OnChanges {
  @Input({ required: true }) title!: string;
  @Input({ required: true }) movies: Movie[] = [];
  // Make the scroller queried after view init (non-static) so template conditionals won't break it
  @ViewChild('scroller', { static: false }) scroller?: ElementRef<HTMLDivElement>;

  private scrollerReady = signal(false);
  private canPrev = signal(false);
  private canNext = signal(false);
  readonly canScroll = computed(() => this.scrollerReady() && this.movies?.length > 0 && (this.canPrev() || this.canNext()));

  // SSR safety: only access window/document when in browser
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);

  // NOTE: We cannot rely on a signal effect for @Input property changes (plain property), so use ngOnChanges.
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['movies']) {
      // If movies provided asynchronously after view init, recalc navigation.
      if (this.scrollerReady()) this.scheduleUpdateNavState();
    }
  }

  ngAfterViewInit() {
    // Mark readiness after view init; microtask ensures QueryList resolution
    queueMicrotask(() => {
      if (this.scroller?.nativeElement) this.scrollerReady.set(true);
      if (this.isBrowser) {
        this.bindListeners();
        this.scheduleUpdateNavState();
      }
    });
  }

  ngOnDestroy() {
    if (this.isBrowser) this.unbindListeners();
  }

  private onScroll = () => this.updateNavState();
  private onResize = () => this.updateNavState();

  private bindListeners() {
    if (!this.isBrowser) return;
    const el = this.scroller?.nativeElement;
    if (el) el.addEventListener('scroll', this.onScroll, { passive: true });
    if (typeof window !== 'undefined') window.addEventListener('resize', this.onResize, { passive: true });
  }
  private unbindListeners() {
    if (!this.isBrowser) return;
    const el = this.scroller?.nativeElement;
    if (el) el.removeEventListener('scroll', this.onScroll);
    if (typeof window !== 'undefined') window.removeEventListener('resize', this.onResize);
  }

  private updateNavState() {
    const el = this.scroller?.nativeElement;
    if (!el) { this.canPrev.set(false); this.canNext.set(false); return; }
    const maxScrollLeft = el.scrollWidth - el.clientWidth;
    // Determine if content actually overflows
    const hasOverflow = maxScrollLeft > 4; // small epsilon
    if (!hasOverflow) { this.canPrev.set(false); this.canNext.set(false); return; }
    this.canPrev.set(el.scrollLeft > 4);
    this.canNext.set(el.scrollLeft < maxScrollLeft - 4);
  }

  scroll(direction: 'prev' | 'next') {
    if (!this.isBrowser) return; // no-op on server
    const el = this.scroller?.nativeElement;
    if (!el) {
      console.warn('[MovieCarousel] scroll requested before scroller ready');
      return;
    }
    // Try to determine single card width (prefer direct child host <app-movie-card>)
    const firstCardHost = el.querySelector<HTMLElement>('app-movie-card');
    const innerCard = el.querySelector<HTMLElement>('.movie-card');
    const cardWidth = innerCard?.offsetWidth || firstCardHost?.offsetWidth || 200;
    // Scroll by ~3 cards or at least 60% of container width
    const scrollAmount = Math.max(cardWidth * 3, el.clientWidth * 0.6);
    el.scrollBy({ left: direction === 'next' ? scrollAmount : -scrollAmount, behavior: 'smooth' });
    // After smooth scroll ends, update nav state (fallback timer)
    setTimeout(() => this.updateNavState(), 400);
  }

  // Public getters for template
  canScrollPrev() { return this.canPrev(); }
  canScrollNext() { return this.canNext(); }

  private scheduleUpdateNavState() {
    // Run multiple times to catch late layout (images/fonts). These are light computations.
    this.updateNavState();
    if (this.isBrowser) {
      requestAnimationFrame(() => this.updateNavState());
      setTimeout(() => this.updateNavState(), 250);
      setTimeout(() => this.updateNavState(), 600);
    }
  }
}