import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent {
  protected readonly auth = inject(AuthService);
  protected readonly sections = signal([
    { title: 'Users', description: 'Manage users and roles', icon: '👥', link: '/admin/users' },
    { title: 'Movies', description: 'Add or update movie catalog', icon: '🎬', link: '/admin/movies' },
    { title: 'Bookings', description: 'Review recent bookings', icon: '🧾', link: '/admin/bookings' },
    { title: 'Theatres', description: 'Manage theatres and locations', icon: '🏛️', link: '/admin/theatres' }
  ]);
}
