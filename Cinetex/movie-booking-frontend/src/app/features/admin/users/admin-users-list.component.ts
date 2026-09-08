import { Component, computed, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UsersApiService, UserSummary, PagedResult } from '../../../core/services/users-api.service';

@Component({
  selector: 'app-admin-users-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users-list.component.html',
  styleUrl: './admin-users-list.component.scss'
})
export class AdminUsersListComponent {
  private readonly api = inject(UsersApiService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly users = signal<UserSummary[]>([]);
  readonly page = signal(0);
  readonly size = signal(20);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);
  readonly search = signal('');
  readonly roleFilter = signal<string | null>(null);
  readonly sort = signal('username,asc');
  readonly selection = signal<Set<string | number>>(new Set());

  readonly allSelected = computed(() => this.users().length > 0 && this.users().every(u => this.selection().has(u.id)));
  readonly someSelected = computed(() => this.users().some(u => this.selection().has(u.id)) && !this.allSelected());

  constructor() {
    effect(() => { this.page(); this.search(); this.roleFilter(); this.sort(); this.reload(); });
  }

  toggleAll(ev: Event) {
    const checked = (ev.target as HTMLInputElement).checked;
    const sel = new Set<string | number>();
    if (checked) this.users().forEach(u => sel.add(u.id));
    this.selection.set(sel);
  }

  toggleOne(id: string | number, ev: Event) {
    const checked = (ev.target as HTMLInputElement).checked;
    const sel = new Set(this.selection());
    if (checked) sel.add(id); else sel.delete(id);
    this.selection.set(sel);
  }

  changePage(delta: number) {
    const next = this.page() + delta;
    if (next < 0 || next >= this.totalPages()) return;
    this.page.set(next);
  }

  applySearch(term: string) { this.page.set(0); this.search.set(term.trim()); }
  setRole(role: string | null) { this.page.set(0); this.roleFilter.set(role); }

  reload() {
    this.loading.set(true); this.error.set(null);
    this.api.list({
      page: this.page(), size: this.size(), sort: this.sort(), role: this.roleFilter() || undefined, search: this.search() || undefined
    }).subscribe({
      next: (res: PagedResult<UserSummary>) => { this.users.set(res.content); this.totalPages.set(res.totalPages); this.totalElements.set(res.totalElements); this.loading.set(false); this.selection.set(new Set()); },
      error: (err: any) => { this.error.set(err?.error?.message || 'Failed to load users'); this.loading.set(false); }
    });
  }

  goDetail(u: UserSummary) { this.router.navigate(['/admin/users', u.id]); }

  bulkDisable() {
    if (!this.selection().size) return;
    if (!confirm(`Disable ${this.selection().size} user(s)?`)) return;
    const ids = Array.from(this.selection());
    this.loading.set(true);
    this.api.bulkDisable(ids).subscribe({
      next: () => { this.loading.set(false); this.reload(); },
      error: (err: any) => { this.error.set(err?.error?.message || 'Bulk disable failed'); this.loading.set(false); }
    });
  }
}
