import { Injectable, effect, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { map, tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { isJwtExpired } from './jwt.utils';

export interface AuthUser {
  username: string;
  email?: string;
  token: string;
}

export interface UserProfile {
  // Optionally provided by backend (adding here to enable features needing userId)
  id?: number;
  username: string;
  email?: string;
  roles?: string[];
  createdAt?: string;
  genrePreference?: string;
  languagePreference?: string;
}

interface LoginRequest { username: string; password: string; }
interface RegisterRequest { username: string; email: string; password: string; }
interface LoginResponse { token: string; username?: string; email?: string; }
interface RegisterResponse { token: string; username?: string; email?: string; }

const STORAGE_KEY = 'auth_user_v1';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http: HttpClient;
  private readonly router: Router;

  // Signals for reactive UI
  readonly user = signal<AuthUser | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly profile = signal<UserProfile | null>(null);
  private returnUrl: string | null = null;
  private profileRequested = false; // prevent duplicate auto fetches

  private readonly platformId = inject(PLATFORM_ID);

  constructor(http: HttpClient, router: Router) {
    this.http = http;
    this.router = router;
    // Hydrate from storage
    if (isPlatformBrowser(this.platformId)) {
      try {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (raw) this.user.set(JSON.parse(raw));
      } catch { /* no-op */ }
    }

    // Persist on change
    effect(() => {
      if (!isPlatformBrowser(this.platformId)) return; // skip on server
      const u = this.user();
      try {
        if (u) localStorage.setItem(STORAGE_KEY, JSON.stringify(u));
        else localStorage.removeItem(STORAGE_KEY);
      } catch { /* ignore quota / access errors */ }
    });

    // Auto-fetch profile when a user with a token exists but profile not yet loaded.
    effect(() => {
      if (!isPlatformBrowser(this.platformId)) return;
      const u = this.user();
      if (u?.token && !this.profile() && !this.loading() && !this.profileRequested) {
        this.profileRequested = true;
        this.getProfile();
      }
    });
  }

  setReturnUrl(url: string) { this.returnUrl = url; }
  consumeReturnUrl(): string | null { const u = this.returnUrl; this.returnUrl = null; return u; }

  login(data: LoginRequest): Observable<AuthUser> {
    this.loading.set(true); this.error.set(null);
  const base = environment.apiBase;
    return this.http.post<LoginResponse>(`${base}/user/login`, data).pipe(
      map(res => {
        const authUser: AuthUser = { username: (res.username ?? data.username), email: res.email, token: res.token };
        return authUser;
      }),
      tap({
        next: user => { this.user.set(user); this.loading.set(false); },
        error: err => { this.loading.set(false); this.error.set(err?.error?.message || 'Login failed'); }
      })
    );
  }

  register(data: RegisterRequest): Observable<AuthUser> {
    this.loading.set(true); this.error.set(null);
  const base = environment.apiBase;
    return this.http.post<RegisterResponse>(`${base}/user/register`, data).pipe(
      map(res => {
        const authUser: AuthUser = { username: (res.username ?? data.username), email: (res.email ?? data.email), token: res.token };
        return authUser;
      }),
      tap({
        next: user => { this.user.set(user); this.loading.set(false); },
        error: err => { this.loading.set(false); this.error.set(err?.error?.message || 'Registration failed'); }
      })
    );
  }

  logout() {
    this.user.set(null);
    this.profile.set(null);
    this.profileRequested = false; // allow auto-fetch for next login
  }

  isExpired(): boolean {
    const u = this.user();
    if (!u?.token) return true;
    return isJwtExpired(u.token);
  }

  getProfile() {
  const base = environment.apiBase;
    const auth = this.user();
    if (!auth?.token) return;
    this.loading.set(true); this.error.set(null);
    this.http.get<UserProfile>(`${base}/user/profile/me`, {
      headers: { Authorization: `Bearer ${auth.token}` }
    }).subscribe({
      next: p => { this.profile.set(p); this.loading.set(false); },
      error: err => { this.loading.set(false); this.error.set(err?.error?.message || 'Failed to load profile'); this.profileRequested = false; }
    });
  }

  updatePassword(newPassword: string) {
  const base = environment.apiBase;
    const auth = this.user();
    if (!auth?.token) return;
    this.loading.set(true); this.error.set(null);
    return this.http.put<{ success?: boolean }>(`${base}/user/profile`, { password: newPassword }, {
      headers: { Authorization: `Bearer ${auth.token}` }
    }).pipe(
      tap({
        next: () => { this.loading.set(false); },
        error: err => { this.loading.set(false); this.error.set(err?.error?.message || 'Failed to update password'); }
      })
    );
  }

  updatePreferences(prefs: { genrePreference?: string; languagePreference?: string; }) {
  const base = environment.apiBase;
    const auth = this.user();
    if (!auth?.token) return;
    this.loading.set(true); this.error.set(null);
    return this.http.put<UserProfile>(`${base}/user/profile`, prefs, {
      headers: { Authorization: `Bearer ${auth.token}` }
    }).pipe(
      tap({
        next: updated => { this.profile.set(updated); this.loading.set(false); },
        error: err => { this.loading.set(false); this.error.set(err?.error?.message || 'Failed to update preferences'); }
      })
    );
  }

  updatePersonal(data: { username?: string; email?: string; }) {
  const base = environment.apiBase;
    const auth = this.user();
    if (!auth?.token) return;
    this.loading.set(true); this.error.set(null);
    return this.http.put<UserProfile>(`${base}/user/profile`, data, {
      headers: { Authorization: `Bearer ${auth.token}` }
    }).pipe(
      tap({
        next: updated => {
          // Update profile
          this.profile.set(updated);
          // Sync auth user basics if changed
          const current = this.user();
            if (current) {
              this.user.set({ ...current, username: updated.username || current.username, email: updated.email || current.email });
            }
          this.loading.set(false);
        },
        error: err => { this.loading.set(false); this.error.set(err?.error?.message || 'Failed to update personal info'); }
      })
    );
  }

  /** Convenience: check if profile has a specific role */
  hasRole(role: string): boolean {
    const p = this.profile();
    if (!p) return false;
    const list: string[] = [];
    // Accept array form
    if (Array.isArray(p.roles)) list.push(...p.roles);
    // Accept singular `role` field if backend uses that shape
    if ((p as any).role && !list.includes((p as any).role)) list.push((p as any).role);
    if (!list.length) return false;
    const target = role.toUpperCase();
    return list.map(r => String(r).toUpperCase()).includes(target);
  }

  /** Return true if the user has ANY of the provided roles */
  hasAnyRole(...roles: string[]): boolean {
    if (!roles.length) return false;
    const p = this.profile();
    if (!p) return false;
    const list: string[] = [];
    if (Array.isArray(p.roles)) list.push(...p.roles);
    if ((p as any).role && !list.includes((p as any).role)) list.push((p as any).role);
    if (!list.length) return false;
    const currentUpper = list.map(r => String(r).toUpperCase());
    return roles.some(r => currentUpper.includes(r.toUpperCase()));
  }
}
