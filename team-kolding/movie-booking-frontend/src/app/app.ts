import { Component, signal, inject, effect, HostListener, ViewChild } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { MoviesMegaMenuComponent } from './shared/components/movies-mega-menu/movies-mega-menu.component';
import { AppFooterComponent } from './shared/components/app-footer/app-footer.component';
import { MovieFiltersService } from './core/services/movie-filters.service';
import { NotificationBellComponent } from './shared/components/notification-bell/notification-bell.component';
import { ToastContainerComponent } from './shared/components/toast-container/toast-container.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MoviesMegaMenuComponent, AppFooterComponent, NotificationBellComponent, ToastContainerComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('movie-booking-frontend');
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly filters = inject(MovieFiltersService);

  // Profile dropdown open state
  protected readonly profileOpen = signal(false);
  protected readonly megaOpen = signal(false);
  private megaHovering = false;
  private megaCloseTimeout: any = null;
  private megaLoaded = false;
  @ViewChild('megaMenu') private megaMenuComp?: any; // reference to MoviesMegaMenuComponent

  openMega() {
    this.megaHovering = true;
    if (this.megaCloseTimeout) { clearTimeout(this.megaCloseTimeout); this.megaCloseTimeout = null; }
    if (!this.megaOpen()) this.megaOpen.set(true);
    // Trigger data load immediately when hovering Movies link (first open)
    // Use setTimeout to ensure ViewChild is resolved after first render cycle when menu becomes visible
    if (this.megaOpen()) {
      setTimeout(() => this.megaMenuComp?.ensureLoaded(), 0);
    }
  }
  scheduleMegaClose() {
    this.megaHovering = false;
    if (this.megaCloseTimeout) clearTimeout(this.megaCloseTimeout);
    this.megaCloseTimeout = setTimeout(() => {
      if (!this.megaHovering) this.megaOpen.set(false);
    }, 180);
  }

  onGenreSelect(g: string) {
    this.filters.patch({ genres: [g], languages: [] });
    this.navigateToMovies();
  }
  onLanguageSelect(l: string) {
    this.filters.patch({ languages: [l], genres: [] });
    this.navigateToMovies();
  }
  private navigateToMovies() {
    this.megaOpen.set(false);
    this.router.navigateByUrl('/movies');
  }

  toggleProfileMenu() {
    this.profileOpen.update(o => !o);
  }

  closeProfileMenu() { this.profileOpen.set(false); }

  // Close on escape key
  @HostListener('document:keydown.escape') onEsc() { if (this.profileOpen()) this.closeProfileMenu(); }

  // Close when clicking outside
  @HostListener('document:click', ['$event']) onDocClick(ev: Event) {
    if (!this.profileOpen()) return;
    const target = ev.target as HTMLElement;
    if (target.closest?.('.profile-menu, .profile-trigger')) return; // inside
    this.closeProfileMenu();
  }

  captureReturnUrl() {
    const current = this.router.url;
    if (!current.startsWith('/login') && !current.startsWith('/register')) {
      this.auth.setReturnUrl(current);
    }
  }

  onLogout() {
    this.auth.logout();
    // Optionally navigate home
    this.router.navigateByUrl('/');
    this.closeProfileMenu();
  }
}
