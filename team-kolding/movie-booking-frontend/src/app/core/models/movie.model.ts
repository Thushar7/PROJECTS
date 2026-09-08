export interface Movie {
  id: string;
  title: string;
  // Legacy singular/plural handling: existing code used `genre` + optional `language`.
  // We are introducing multi-value capable `genres` and `languages` while preserving
  // the old fields for backward compatibility during migration.
  genre: string[]; // legacy primary genres array (kept)
  language?: string; // legacy single language (first of languages[] if provided)
  genres?: string[]; // new plural field (preferred going forward)
  languages?: string[]; // new plural field (preferred going forward)
  posterUrl?: string;
  bannerUrl?: string;
  rating: number; // 0-10 scale
  year: number;
  durationMinutes: number;
  description: string;
  releaseDate?: string; // optional raw release_date string if needed
  // Internal (non-UI) enrichment metadata for mapping id arrays to names
  _genreIds?: number[];
  _languageIds?: number[];
}

export interface MovieCategory {
  key: string;
  label: string;
  movies: Movie[];
}