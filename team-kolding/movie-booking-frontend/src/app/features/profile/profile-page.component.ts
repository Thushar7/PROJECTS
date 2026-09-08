import { Component, inject, OnInit, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { RouterModule } from '@angular/router';
import { GenresApiService } from '../../core/services/genres-api.service';
import { LanguagesApiService } from '../../core/services/languages-api.service';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-profile-page',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.scss']
})
export class ProfilePageComponent implements OnInit {
  private auth = inject(AuthService);
  protected get authService() { return this.auth; }
  private genresApi = inject(GenresApiService);
  private languagesApi = inject(LanguagesApiService);

  // Local state for select options
  protected readonly genres = signal<string[]>([]);
  protected readonly languages = signal<string[]>([]);

  // Form model
  protected genrePreference = signal<string | undefined>(undefined);
  protected languagePreference = signal<string | undefined>(undefined);
  protected saving = signal(false);
  protected saveSuccess = signal(false);
  protected personalEditing = signal(false);
  protected personalSaving = signal(false);
  protected personalSuccess = signal(false);
  protected editUsername = signal('');
  protected editEmail = signal('');

  protected readonly canSubmit = computed(() => !this.auth.loading() && !this.saving());
  protected readonly activeTab = signal<'personal' | 'preferences' | 'security'>('personal');

  // React to profile arrival (must be declared in injection context, not inside ngOnInit)
  private readonly profileEffect = effect(() => {
    const p = this.auth.profile();
    if (p) {
      this.genrePreference.set(p.genrePreference);
      this.languagePreference.set(p.languagePreference);
      // Initialize personal edit fields when profile first loads (only if not already editing)
      if (!this.personalEditing()) {
        this.editUsername.set(p.username);
        this.editEmail.set(p.email || '');
      }
    }
  });

  setTab(tab: 'personal' | 'preferences' | 'security') { this.activeTab.set(tab); this.saveSuccess.set(false); }

  ngOnInit(): void {
    // Fetch profile if not already loaded
    if (!this.auth.profile()) {
      this.auth.getProfile();
    }
    // Load option lists (fire and forget)
    this.genresApi.getAll().subscribe(list => this.genres.set(list));
    this.languagesApi.getAll().subscribe(list => this.languages.set(list));

  }

  onSavePreferences() {
    if (!this.canSubmit()) return;
    this.saving.set(true); this.saveSuccess.set(false);
    this.auth.updatePreferences({
      genrePreference: this.genrePreference(),
      languagePreference: this.languagePreference()
    })?.subscribe({
      next: () => { this.saving.set(false); this.saveSuccess.set(true); },
      error: () => { this.saving.set(false); }
    });
  }

  startEditPersonal() {
    const p = this.auth.profile();
    if (!p) return;
    this.editUsername.set(p.username);
    this.editEmail.set(p.email || '');
    this.personalEditing.set(true);
    this.personalSuccess.set(false);
  }

  cancelEditPersonal() {
    const p = this.auth.profile();
    if (p) {
      this.editUsername.set(p.username);
      this.editEmail.set(p.email || '');
    }
    this.personalEditing.set(false);
  }

  savePersonal() {
    if (this.personalSaving()) return;
    const username = this.editUsername().trim();
    const email = this.editEmail().trim();
    if (!username || !email) return; // basic validation
    this.personalSaving.set(true);
    this.personalSuccess.set(false);
    this.auth.updatePersonal({ username, email })?.subscribe({
      next: () => { this.personalSaving.set(false); this.personalSuccess.set(true); this.personalEditing.set(false); },
      error: () => { this.personalSaving.set(false); }
    });
  }
}
