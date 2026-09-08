import { Injectable, signal } from '@angular/core';

export interface ToastOptions {
  id?: number;
  type?: 'info' | 'success' | 'warning' | 'error';
  message: string;
  autoCloseMs?: number; // default 5000
  dismissible?: boolean; // default true
}

export interface Toast extends Required<Omit<ToastOptions, 'id'>> { id: number; }

let _nextId = 1;

@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly toasts = signal<Toast[]>([]);

  show(opts: ToastOptions) {
    const toast: Toast = {
      id: opts.id ?? _nextId++,
      type: opts.type ?? 'info',
      message: opts.message,
      autoCloseMs: opts.autoCloseMs ?? 5000,
      dismissible: opts.dismissible ?? true
    };
    this.toasts.update(list => [...list, toast]);
    if (toast.autoCloseMs > 0) {
      setTimeout(() => this.dismiss(toast.id), toast.autoCloseMs);
    }
    return toast.id;
  }

  info(message: string, opts: Omit<ToastOptions, 'message' | 'type'> = {}) { return this.show({ ...opts, type: 'info', message }); }
  success(message: string, opts: Omit<ToastOptions, 'message' | 'type'> = {}) { return this.show({ ...opts, type: 'success', message }); }
  warning(message: string, opts: Omit<ToastOptions, 'message' | 'type'> = {}) { return this.show({ ...opts, type: 'warning', message }); }
  error(message: string, opts: Omit<ToastOptions, 'message' | 'type'> = {}) { return this.show({ ...opts, type: 'error', message }); }

  dismiss(id: number) { this.toasts.update(list => list.filter(t => t.id !== id)); }
  clear() { this.toasts.set([]); }
}
