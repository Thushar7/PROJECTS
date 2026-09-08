export interface Seat {
  id: string;       // e.g. A1
  row: string;      // e.g. A
  number: number;   // e.g. 1
  reserved: boolean;
  selected: boolean;
}

export interface SeatLayoutOptions {
  rows?: string[];          // default A-J
  seatsPerRow?: number;     // default 12
  reservedSeats?: string[]; // e.g. ["A1","B4"]
  selectedSeats?: string[]; // preselected if needed
}

/**
 * Generate a theatre seat layout client-side.
 * For now uses static if theatre metadata not available.
 */
export function generateSeatLayout(opts: SeatLayoutOptions = {}): Seat[] {
  const rows = opts.rows || Array.from({ length: 10 }, (_, i) => String.fromCharCode(65 + i)); // A-J
  const seatsPerRow = opts.seatsPerRow ?? 12;
  const reserved = new Set((opts.reservedSeats || []).map(s => s.toUpperCase()));
  const preselected = new Set((opts.selectedSeats || []).map(s => s.toUpperCase()));
  const layout: Seat[] = [];
  for (const row of rows) {
    for (let n = 1; n <= seatsPerRow; n++) {
      const id = `${row}${n}`;
      layout.push({
        id,
        row,
        number: n,
        reserved: reserved.has(id.toUpperCase()),
        selected: preselected.has(id.toUpperCase()) && !reserved.has(id.toUpperCase())
      });
    }
  }
  return layout;
}

export function toggleSeat(layout: Seat[], seatId: string, max?: number): Seat[] {
  return layout.map(s => {
    if (s.id !== seatId) return s;
    if (s.reserved) return s; // cannot toggle
    if (!s.selected) {
      if (typeof max === 'number' && layout.filter(x => x.selected).length >= max) {
        return s; // ignore if max reached
      }
      return { ...s, selected: true };
    }
    return { ...s, selected: false };
  });
}

export function selectedSeatIds(layout: Seat[]): string[] {
  return layout.filter(s => s.selected).map(s => s.id);
}
