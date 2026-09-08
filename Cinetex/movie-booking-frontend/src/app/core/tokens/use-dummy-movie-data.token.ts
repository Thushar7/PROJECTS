import { InjectionToken } from '@angular/core';

// Toggle for using local dummy movie data instead of hitting the API service.
// Default factory returns true so tests / local dev can work without backend.
export const USE_DUMMY_MOVIE_DATA = new InjectionToken<boolean>('USE_DUMMY_MOVIE_DATA', {
  factory: () => true
});
