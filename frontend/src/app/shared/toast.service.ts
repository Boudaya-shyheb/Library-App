import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface ToastMessage {
  type: 'success' | 'error' | 'info';
  text: string;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private readonly toastSubject = new BehaviorSubject<ToastMessage | null>(null);
  readonly toast$ = this.toastSubject.asObservable();

  show(type: ToastMessage['type'], text: string): void {
    this.toastSubject.next({ type, text });
    setTimeout(() => this.toastSubject.next(null), 3000);
  }
}
