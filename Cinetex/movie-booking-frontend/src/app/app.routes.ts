import { Routes } from '@angular/router';
import { HomePageComponent } from './features/home/home-page.component';
import { MoviesListPageComponent } from './features/movies/movies-list-page.component';
import { LoginPageComponent } from './features/login/login-page.component';
import { RegisterPageComponent } from './features/register/register-page.component';
import { ProfilePageComponent } from './features/profile/profile-page.component';
import { ResetPasswordPageComponent } from './features/reset-password/reset-password-page.component';
import { authGuard } from './core/services/auth.guard';
import { adminGuard } from './core/services/admin.guard';
import { AdminDashboardComponent } from './features/admin/admin-dashboard.component';
import { AdminUsersListComponent } from './features/admin/users/admin-users-list.component';
import { ShowtimesPageComponent } from './features/showtimes/showtimes-page.component';
import { SeatBookingPageComponent } from './features/booking/seat-booking-page.component';

export const routes: Routes = [
	{ path: '', component: HomePageComponent, title: 'Home - Movie Booking' },
	{ path: 'movies', component: MoviesListPageComponent, title: 'All Movies' },
	{ path: 'login', component: LoginPageComponent, title: 'Login' },
	{ path: 'register', component: RegisterPageComponent, title: 'Register' },
	{ path: 'profile', component: ProfilePageComponent, canActivate: [authGuard], title: 'Your Profile' },
	{ path: 'reset-password', component: ResetPasswordPageComponent, canActivate: [authGuard], title: 'Reset Password' },
	{ path: 'showtimes/:movieId', component: ShowtimesPageComponent, title: 'Showtimes' },
	{ path: 'booking/:showtimeId', component: SeatBookingPageComponent, title: 'Book Seats' },
	{ path: 'my-bookings', canActivate: [authGuard], loadComponent: () => import('./features/bookings/my-bookings-page.component').then(m => m.MyBookingsPageComponent), title: 'My Bookings' },
	// Admin area (lazy-ish via standalone components)
	{ path: 'admin', canActivate: [authGuard, adminGuard], children: [
		{ path: '', component: AdminDashboardComponent, title: 'Admin Dashboard' },
		{ path: 'users', component: AdminUsersListComponent, title: 'Manage Users' },
		// Future placeholders for movies, bookings, theatres
		{ path: 'movies', loadComponent: () => import('./features/admin/movies/admin-movies-list.component').then(m => m.AdminMoviesListComponent), title: 'Manage Movies' },
		{ path: 'movies/add', loadComponent: () => import('./features/admin/movies/add-movie-page.component').then(m => m.AddMoviePageComponent), title: 'Add Movie' },
		{ path: 'movies/:movieId/poster', loadComponent: () => import('./features/admin/movies/movie-poster-upload-page.component').then(m => m.MoviePosterUploadPageComponent), title: 'Upload Movie Poster' },
		{ path: 'bookings', loadComponent: () => import('./features/admin/bookings/admin-bookings-list.component').then(m => m.AdminBookingsListComponent), title: 'All Bookings' },
		{ path: 'theatres', loadComponent: () => import('./features/admin/theatres/admin-theatres-list.component').then(m => m.AdminTheatresListComponent), title: 'Theatres' }
	]},
	{ path: '**', redirectTo: '' }
];
