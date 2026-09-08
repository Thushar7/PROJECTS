import { Component, Input, Output, EventEmitter, inject, computed, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Movie } from '../../../core/models/movie.model';
import { TheatreDto } from '../../../core/services/theatres-api.service';
import { ShowTimeDto } from '../../../core/services/showtimes-api.service';
import { BookingDto } from '../../../core/services/booking-api.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  standalone: true,
  selector: 'app-ticket-modal',
  imports: [CommonModule],
  templateUrl: './ticket-modal.component.html',
  styleUrls: ['./ticket-modal.component.scss']
})
export class TicketModalComponent {
  private auth = inject(AuthService);

  @Input({ required: true }) booking!: BookingDto;
  @Input({ required: true }) movie!: Movie;
  @Input({ required: true }) theatre!: TheatreDto;
  @Input({ required: true }) showtime!: ShowTimeDto;
  @Input() posterSrc: string | null = null;

  @Output() close = new EventEmitter<void>();

  @ViewChild('ticketRef') ticketRef!: ElementRef<HTMLElement>;

  readonly userName = computed(() => this.auth.user()?.username || 'Guest');

  formatTime(dateStr: string) {
    try { return new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }); } catch { return dateStr; }
  }

  async downloadPdf() {
    // Lazy load libs only when needed
    try {
      const [{ default: html2canvas }, jsPDFModule] = await Promise.all([
        import('html2canvas'),
        import('jspdf')
      ]);
      const jsPDF = (jsPDFModule as any).jsPDF || jsPDFModule.default || (window as any).jsPDF;
      const el = this.ticketRef?.nativeElement;
      if (!el) return;
      const canvas = await html2canvas(el, { scale: 2, backgroundColor: '#ffffff0' });
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF({ orientation: 'portrait', unit: 'pt', format: 'a4' });
      const pageWidth = pdf.internal.pageSize.getWidth();
      const ticketWidth = pageWidth - 80; // 40pt margins
      const ratio = ticketWidth / canvas.width;
      const imgHeight = canvas.height * ratio;
      pdf.addImage(imgData, 'PNG', 40, 40, ticketWidth, imgHeight);
      pdf.save(`ticket-${this.booking.bookingId}.pdf`);
    } catch (e) {
      console.error('PDF export failed', e);
      alert('Failed to generate PDF.');
    }
  }

  emitClose() { this.close.emit(); }
}
