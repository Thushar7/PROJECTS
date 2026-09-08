import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection, inject } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { AuthService } from './core/services/auth.service';
import { USE_DUMMY_MOVIE_DATA } from './core/tokens/use-dummy-movie-data.token';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    provideHttpClient(
      withFetch(),
      withInterceptors([
      (req, next) => {
        // Prefer live auth service signal value to avoid race with persistence effect
        let token: string | null | undefined = inject(AuthService).user()?.token;
        if (!token) {
          // Fallback to storage (e.g., during initial hydration before DI graph stable)
            try {
              const raw = localStorage.getItem('auth_user_v1');
              if (raw) token = JSON.parse(raw)?.token || null;
            } catch { /* ignore */ }
        }
        if (token) req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
        return next(req);
      }
    ])),
    { provide: USE_DUMMY_MOVIE_DATA, useValue: false }
  ]
};
