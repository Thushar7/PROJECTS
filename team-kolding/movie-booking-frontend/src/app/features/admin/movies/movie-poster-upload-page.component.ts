import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MoviesApiService } from '../../../core/services/movies-api.service';

@Component({
  selector: 'app-movie-poster-upload-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './movie-poster-upload-page.component.html',
  styleUrls: ['./movie-poster-upload-page.component.scss']
})
export class MoviePosterUploadPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly moviesApi = inject(MoviesApiService);

  protected readonly movieId = signal<string>('');
  protected readonly file = signal<File | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly uploading = signal(false);
  protected readonly previewUrl = signal<string | null>(null);

  readonly canUpload = computed(() => !!this.file() && !this.uploading());

  constructor() {
    const id = this.route.snapshot.paramMap.get('movieId');
    if (!id) {
      this.error.set('Invalid movie id');
    } else {
      this.movieId.set(id);
    }
  }

  onFileChange(ev: Event) {
    const input = ev.target as HTMLInputElement;
    if (!input.files || !input.files.length) {
      this.file.set(null);
      this.previewUrl.set(null);
      return;
    }
    const f = input.files[0];
    if (!/image\/(jpeg|jpg|png)/i.test(f.type)) {
      this.error.set('Only JPG or PNG images are allowed');
      this.file.set(null);
      this.previewUrl.set(null);
      return;
    }
    this.error.set(null);
    this.file.set(f);
    const reader = new FileReader();
    reader.onload = () => this.previewUrl.set(reader.result as string);
    reader.readAsDataURL(f);
  }

  upload() {
    if (!this.canUpload()) return;
    this.uploading.set(true);
    this.moviesApi.uploadPoster(this.movieId(), this.file()!).subscribe({
      next: () => {
        this.uploading.set(false);
        // After successful upload, navigate to movies list (could also navigate to details page later)
        this.router.navigate(['/movies']);
      },
      error: err => {
        console.error('Poster upload failed', err);
        this.uploading.set(false);
        this.error.set('Failed to upload poster');
      }
    });
  }
}
