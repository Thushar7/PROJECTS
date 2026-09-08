import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

/** Guard that allows only ADMIN role users to proceed. Redirects to home otherwise. */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const isAdmin = auth.hasRole('ADMIN');
  return isAdmin || (router.createUrlTree(['/']));
};
