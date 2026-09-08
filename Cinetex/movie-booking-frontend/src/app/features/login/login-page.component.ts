import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-login-page',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  username = '';
  password = '';
  readonly justRegistered = signal(false);

  constructor() {
    this.route.queryParamMap.subscribe(pm => {
      this.justRegistered.set(pm.get('registered') === '1');
    });
  }

  onSubmit() {
    if (!this.username || !this.password) return;
    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        const back = this.auth.consumeReturnUrl();
        this.router.navigateByUrl(back || '/');
      }
    });
  }
}
