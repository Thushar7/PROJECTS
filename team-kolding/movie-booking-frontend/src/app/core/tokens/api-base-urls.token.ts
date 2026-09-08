import { InjectionToken } from '@angular/core';
import { environment } from '../../../environments/environment';

// Unified API base token – prefer this in new code.
export const API_BASE_URL = new InjectionToken<string>(
  'API_BASE_URL',
  { providedIn: 'root', factory: () => environment.apiBase }
);

// Legacy tokens (retain for incremental migration)
export const MOVIES_API_BASE_URL = new InjectionToken<string>(
  'MOVIES_API_BASE_URL',
  { providedIn: 'root', factory: () => environment.api.movies }
);
export const MOVIES_FILTER_API_BASE_URL = new InjectionToken<string>(
  'MOVIES_FILTER_API_BASE_URL',
  { providedIn: 'root', factory: () => environment.api.moviesFilter }
);
