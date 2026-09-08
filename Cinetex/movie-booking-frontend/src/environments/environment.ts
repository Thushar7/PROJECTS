// Unified API base.
// Legacy keys retained temporarily for backward compatibility; prefer environment.apiBase.
const base = 'http://localhost:8080';
export const environment = {
  apiBase: base,
  // legacy structure (to be removed once all services migrated)
  api: {
    movies: base,
    moviesFilter: base,
    bookings: base
  }
};
