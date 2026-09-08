import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TheatresApiService, TheatreDto } from '../../../core/services/theatres-api.service';

interface TheatreRow { theatreId: number; name: string; city?: string; state?: string; }

@Component({
  selector: 'app-admin-theatres-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-theatres-list.component.html',
  styleUrl: './admin-theatres-list.component.scss'
})
export class AdminTheatresListComponent {
  private readonly api = inject(TheatresApiService);
  readonly theatres = signal<TheatreRow[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly creating = signal(false);
  readonly createError = signal<string | null>(null);
  // Form model
  newName = '';
  newCity = '';
  newState = '';
  // Modal state
  readonly showModal = signal(false);

  constructor() { this.load(); }

  load() {
    this.loading.set(true); this.error.set(null);
    this.api.list().subscribe({
      next: list => { this.theatres.set(list); this.loading.set(false); },
      error: err => { this.error.set(err?.error?.message || 'Failed to load theatres'); this.loading.set(false); }
    });
  }

  canSubmit(): boolean {
    return !this.creating() && this.newName.trim().length > 0;
  }

  submit() {
    if (!this.canSubmit()) return;
    const payload = {
      name: this.newName.trim(),
      city: this.newCity.trim() || undefined,
      state: this.newState.trim() || undefined
    };
    this.creating.set(true); this.createError.set(null);
    this.api.create(payload).subscribe({
      next: created => {
        // Prepend or append new theatre
        this.theatres.set([...(this.theatres()), created]);
        this.newName = ''; this.newCity = ''; this.newState = '';
        this.creating.set(false);
        this.showModal.set(false);
      },
      error: err => { this.createError.set(err?.error?.message || 'Create failed'); this.creating.set(false); }
    });
  }

  openModal() { this.showModal.set(true); this.createError.set(null); setTimeout(() => {
    const el = document.querySelector('#theatre-name-input') as HTMLInputElement | null; el?.focus();
  }, 0); }
  closeModal() { if (!this.creating()) { this.showModal.set(false); } }
  onKeydown(ev: KeyboardEvent) { if (ev.key === 'Escape') { this.closeModal(); } }
}
