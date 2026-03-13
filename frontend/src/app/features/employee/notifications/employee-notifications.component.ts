import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EmployeeService } from '../../../core/services/employee.service';

@Component({
  selector: 'app-employee-notifications',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule, MatSnackBarModule, MatTooltipModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1>Notifications</h1>
        <div style="display:flex;gap:10px;align-items:center">
          <span *ngIf="unreadCount>0"
            style="background:#fce4ec;color:#c62828;padding:6px 14px;border-radius:20px;font-size:13px;font-weight:600">
            {{ unreadCount }} Unread
          </span>
          <button *ngIf="unreadCount>0" mat-stroked-button (click)="markAllRead()"
            style="color:#4a148c;border-color:#4a148c;font-size:13px">
            <mat-icon style="font-size:16px">done_all</mat-icon> Mark All Read
          </button>
        </div>
      </div>

      <div *ngIf="loading" class="loading-overlay"><mat-spinner></mat-spinner></div>

      <div *ngIf="!loading && notifications.length===0" class="empty-state">
        <mat-icon>notifications_off</mat-icon>
        <p>No notifications yet</p>
        <p style="font-size:13px;color:#aaa">Leave approvals, goal assignments and announcements appear here</p>
      </div>

      <div *ngFor="let n of notifications" class="card"
        style="margin-bottom:10px;padding:16px;display:flex;align-items:flex-start;gap:12px;cursor:pointer;border-left:4px solid;transition:all 0.2s"
        [style.border-left-color]="n.isRead?'#e0e0e0':'#4a148c'"
        [style.background]="n.isRead?'#fff':'#faf4ff'"
        (click)="markRead(n)"
        matTooltip="{{ n.isRead ? 'Already read' : 'Click to mark as read' }}">

        <div style="width:38px;height:38px;border-radius:50%;display:flex;align-items:center;justify-content:center;flex-shrink:0"
          [style.background]="n.isRead?'#f5f5f5':'#f3e5f5'">
          <mat-icon [style.color]="n.isRead?'#bbb':'#4a148c'" style="font-size:18px;width:18px;height:18px">
            {{ getNotifIcon(n) }}
          </mat-icon>
        </div>

        <div style="flex:1">
          <p style="font-size:14px;margin:0 0 4px;line-height:1.5"
            [style.color]="n.isRead?'#888':'#1a1a2e'"
            [style.font-weight]="n.isRead?'400':'600'">
            {{ n.message }}
          </p>
          <div style="font-size:11px;color:#aaa;display:flex;align-items:center;gap:4px">
            <mat-icon style="font-size:12px;width:12px;height:12px">schedule</mat-icon>
            {{ n.createdAt | date:'dd MMM yyyy, h:mm a' }}
          </div>
        </div>

        <div *ngIf="!n.isRead" style="width:9px;height:9px;border-radius:50%;background:#4a148c;margin-top:6px;flex-shrink:0"></div>
      </div>
    </div>
  `
})
export class EmployeeNotificationsComponent implements OnInit {
  notifications: any[] = [];
  loading = true;

  constructor(private employeeService: EmployeeService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    setTimeout(() => { this.loading = false; }, 8000); this.load(); }

  load(): void {
    this.loading = true;
    this.employeeService.getNotifications().subscribe({
      next: (d: any) => { this.notifications = Array.isArray(d)?d:(d?.data||[]); this.loading=false; },
      error: () => { this.loading=false; }
    });
  }

  get unreadCount(): number { return this.notifications.filter(n=>!n.isRead).length; }

  markRead(n: any): void {
    if (n.isRead) return;
    this.employeeService.markNotificationRead(n.id).subscribe({
      next: () => { n.isRead = true; },
      error: () => {}
    });
  }

  markAllRead(): void {
    this.employeeService.markAllNotificationsRead().subscribe({
      next: () => { this.notifications.forEach(n=>n.isRead=true); this.snackBar.open('All marked as read','Close',{duration:1500}); },
      error: () => {}
    });
  }

  getNotifIcon(n: any): string {
    const msg = (n.message||'').toLowerCase();
    if (msg.includes('leave')) return 'event_note';
    if (msg.includes('goal')) return 'flag';
    if (msg.includes('review') || msg.includes('performance')) return 'star_rate';
    if (msg.includes('announce')) return 'campaign';
    return 'notifications_active';
  }
}