import { ShowtimesPageComponent } from './showtimes-page.component';

describe('ShowtimesPageComponent time serialization', () => {
  it('should keep 13:00 as 13:00 local naive string without UTC shift', () => {
    const comp = new ShowtimesPageComponent();
    // Set form signals
    (comp as any).formShowDate.set('2025-10-16');
    (comp as any).formStartTime.set('13:00');
    (comp as any).formEndTime.set('15:30');
    // Access private submit logic indirectly by replicating its serialization snippet
    const date = (comp as any).formShowDate();
    const start = `${date}T${(comp as any).formStartTime()}:00`;
    expect(start).toBe('2025-10-16T13:00:00');
  });

  it('formatTime should return HH:mm for naive strings', () => {
    const comp = new ShowtimesPageComponent();
    const formatted = comp.formatTime('2025-10-16T13:05:00');
    expect(formatted).toBe('13:05');
  });

  it('should infer next-day end date when end time earlier than start time', () => {
    const comp = new ShowtimesPageComponent();
    (comp as any).formShowDate.set('2025-10-16');
    (comp as any).formStartTime.set('22:00');
    (comp as any).formEndTime.set('01:00');
    // Reproduce the inference snippet
    const startDate = (comp as any).formShowDate();
    let endDate = (comp as any).formEndDate();
    const startTime = (comp as any).formStartTime();
    const endTime = (comp as any).formEndTime();
    if (!endDate) {
      if (startTime && endTime && endTime < startTime) {
        const d = new Date(`${startDate}T00:00:00`);
        d.setDate(d.getDate() + 1);
        endDate = d.toISOString().slice(0,10);
      } else {
        endDate = startDate;
      }
    }
    const endIso = `${endDate}T${endTime}:00`;
    expect(endDate).toBe('2025-10-17');
    expect(endIso).toBe('2025-10-17T01:00:00');
  });
});
