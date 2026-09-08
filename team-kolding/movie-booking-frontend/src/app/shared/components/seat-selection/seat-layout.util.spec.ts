import { generateSeatLayout, toggleSeat, selectedSeatIds } from './seat-layout.util';

describe('seat-layout.util', () => {
  it('generates default layout (A-J, 12 seats each)', () => {
    const layout = generateSeatLayout();
    expect(layout.length).toBe(10 * 12);
    expect(layout[0].id).toBe('A1');
    expect(layout.at(-1)?.id).toBe('J12');
  });

  it('marks reserved seats', () => {
    const layout = generateSeatLayout({ reservedSeats:['A1','B2'] });
    const a1 = layout.find(s => s.id==='A1');
    const b2 = layout.find(s => s.id==='B2');
    expect(a1?.reserved).toBeTrue();
    expect(b2?.reserved).toBeTrue();
  });

  it('toggles selection respecting max', () => {
    let layout = generateSeatLayout();
    layout = toggleSeat(layout,'A1',2);
    layout = toggleSeat(layout,'A2',2);
    layout = toggleSeat(layout,'A3',2); // should be ignored (max 2)
    expect(selectedSeatIds(layout)).toEqual(['A1','A2']);
    layout = toggleSeat(layout,'A1',2); // unselect A1
    layout = toggleSeat(layout,'A3',2); // now A3 allowed
    expect(selectedSeatIds(layout)).toEqual(['A2','A3']);
  });
});
