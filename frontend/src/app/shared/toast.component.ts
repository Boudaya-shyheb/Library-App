import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="toast$ | async as toast" class="toast" [class.error]="toast.type === 'error'" [class.success]="toast.type === 'success'">
      {{ toast.text }}
    </div>
  `,
  styles: [`
    .toast { position: fixed; top: 16px; right: 16px; padding: 10px 14px; border-radius: 8px; background:#334155; color:#fff; z-index:1000; }
    .toast.success { background:#166534; }
    .toast.error { background:#991b1b; }
  `]
})
export class ToastComponent {
  readonly toast$ = this.toastService.toast$;
  constructor(private toastService: ToastService) {}
}
