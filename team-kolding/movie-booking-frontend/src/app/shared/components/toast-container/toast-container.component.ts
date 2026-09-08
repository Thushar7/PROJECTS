import { Component, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, Toast } from '../../../core/services/toast.service';

@Component({
  standalone: true,
  selector: 'app-toast-container',
  imports: [CommonModule],
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.scss']
})
export class ToastContainerComponent {
  private svc = inject(ToastService);
  toasts = this.svc.toasts; // signal

  dismiss(id: number) { this.svc.dismiss(id); }
  trackById = (_: number, t: Toast) => t.id;

  iconFor(type: string): string {
    switch (type) {
      case 'success': return '✔';
      case 'warning': return '⚠';
      case 'error': return '⛔';
      default: return 'ℹ';
    }
  }
}
