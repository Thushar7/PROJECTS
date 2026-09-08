import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { GenresApiService } from '../../core/services/genres-api.service';
import { LanguagesApiService } from '../../core/services/languages-api.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-register-page',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss']
})
export class RegisterPageComponent {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private genresApi = inject(GenresApiService);
  private languagesApi = inject(LanguagesApiService);
  username = '';
  email = '';
  password = '';
  genrePreference = '';
  languagePreference = '';
  step = 1; // 1 = credentials, 2 = preferences

  // Option lists
  genres = signal<string[]>([]);
  languages = signal<string[]>([]);

  constructor() {
    // Load option lists (best-effort; ignore errors silently)
    this.genresApi.getAll().subscribe({ next: list => this.genres.set(list.slice().sort()) });
    this.languagesApi.getAll().subscribe({ next: list => this.languages.set(list.slice().sort()) });
  }

  nextStep() {
    if (this.step !== 1) return;
    if (!this.username || !this.email || !this.password) return;
    this.step = 2;
  }

  skipPreferences() {
    // finalize without preferences
    this.finalizeRegistration();
  }

  finalizeRegistration() {
    if (!this.username || !this.email || !this.password) {
      this.step = 1;
      return;
    }
    const payload: any = { username: this.username, email: this.email, password: this.password };
    if (this.genrePreference) payload.genrePreference = this.genrePreference;
    if (this.languagePreference) payload.languagePreference = this.languagePreference;
    this.auth.register(payload).subscribe({
      next: () => {
        // Redirect user to login page after successful registration
        this.router.navigate(['/login'], { queryParams: { registered: '1' } });
      }
    });
  }

  backStep() {
    if (this.step === 2) {
      this.step = 1;
    }
  }
}
