import { Component, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationsApiService, NotificationDto } from '../../../core/services/notifications-api.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  standalone: true,
  selector: 'app-notification-bell',
  imports: [CommonModule],
  templateUrl: './notification-bell.component.html',
  styleUrls: ['./notification-bell.component.scss']
})
export class NotificationBellComponent {
  private api = inject(NotificationsApiService);
  private auth = inject(AuthService);

  readonly open = signal(false);
  readonly loading = signal(false);
  readonly items = signal<NotificationDto[]>([]);
  readonly error = signal<string | null>(null);
  readonly readIds = signal<Set<number>>(new Set());

  toggle() {
    const willOpen = !this.open();
    this.open.set(willOpen);
    if (willOpen) {
      // Always fetch latest when user opens the panel
      this.fetch();
    }
  }

  unreadCount() {
    const read = this.readIds();
    return this.items().reduce((acc, n) => acc + (read.has(n.id) ? 0 : 1), 0);
  }

  private fetch() {
    const profile = this.auth.profile();
    const id = profile?.id;
    if (!id) { this.error.set('Login to view notifications'); return; }
    this.loading.set(true); this.error.set(null);
    this.api.listForUser(id).subscribe({
      next: list => { this.items.set(list); this.loading.set(false); },
      error: err => { this.error.set(err?.error?.message || 'Failed to load notifications'); this.loading.set(false); }
    });
  }

  formatTime(ts: string) {
    try { return new Date(ts).toLocaleString([], { hour: '2-digit', minute: '2-digit', day: '2-digit', month: 'short' }); } catch { return ts; }
  }
}
