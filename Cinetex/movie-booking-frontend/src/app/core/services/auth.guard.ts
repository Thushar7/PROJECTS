import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { isJwtExpired } from './jwt.utils';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const current = auth.user();
  if (current?.token) {
    if (!isJwtExpired(current.token)) {
      return true;
    }
    // Token expired – clear session and redirect to login
    auth.logout();
  }
  auth.setReturnUrl(state.url);
  return router.createUrlTree(['/login']);
};
