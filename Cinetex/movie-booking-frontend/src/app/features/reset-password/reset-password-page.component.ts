import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-reset-password-page',
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-password-page.component.html',
  styleUrls: ['./reset-password-page.component.scss']
})
export class ResetPasswordPageComponent {
  protected auth = inject(AuthService);
  private router = inject(Router);

  password = '';
  confirm = '';
  success = false;

  onSubmit() {
    if (!this.password || this.password.length < 4) return;
    if (this.password !== this.confirm) return;
    const obs = this.auth.updatePassword(this.password);
    if (!obs) return;
    obs.subscribe({
      next: () => { this.success = true; this.password = this.confirm = ''; },
    });
  }

  goBack() { this.router.navigateByUrl('/profile'); }
}
