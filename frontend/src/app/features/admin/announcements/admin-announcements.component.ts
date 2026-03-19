import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AdminService } from '../../../core/services/admin.service';
import { Announcement } from '../../../core/models/models';

@Component({
  selector: 'app-admin-announcements',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatCardModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatInputModule,
    MatCheckboxModule, MatSnackBarModule, MatProgressSpinnerModule
  ],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1>Announcements</h1>
        <button mat-flat-button color="primary" (click)="openForm()" *ngIf="!showForm">
          <mat-icon>add</mat-icon> New Announcement
        </button>
      </div>

      <!-- Create Form -->
      <div class="card" *ngIf="showForm" style="margin-bottom:20px">
        <h3 style="margin-bottom:20px;font-weight:600;color:#1a1a2e">Create Announcement</h3>
        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Title</mat-label>
            <input matInput formControlName="title">
          </mat-form-field>
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Content</mat-label>
            <textarea matInput formControlName="content" rows="4"></textarea>
          </mat-form-field>
          <mat-checkbox formControlName="isActive" style="margin-bottom:16px">
            Make Active (visible to all employees)
          </mat-checkbox>
          <div style="display:flex;gap:12px;justify-content:flex-end">
            <button mat-button type="button" (click)="closeForm()">Cancel</button>
            <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid || saving">
              {{ saving ? 'Publishing...' : 'Publish Announcement' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="loading-overlay"><mat-spinner></mat-spinner></div>

      <!-- Announcements List -->
      <ng-container *ngIf="!loading">
        <div *ngIf="announcements.length === 0 && !showForm" class="empty-state">
          <mat-icon>campaign</mat-icon>
          <p>No announcements yet. Create one to broadcast to all employees.</p>
          <button mat-flat-button color="primary" (click)="openForm()">
            <mat-icon>add</mat-icon> Create Announcement
          </button>
        </div>

        <div *ngFor="let a of announcements" class="card announcement-card" style="margin-bottom:16px">
          <div style="display:flex;justify-content:space-between;align-items:flex-start">
            <div style="flex:1">
              <div style="display:flex;align-items:center;gap:10px;margin-bottom:8px">
                <div style="width:40px;height:40px;border-radius:10px;
                  background:linear-gradient(135deg,#4a148c,#7b1fa2);
                  display:flex;align-items:center;justify-content:center">
                  <mat-icon style="color:#fff;font-size:20px;width:20px;height:20px">campaign</mat-icon>
                </div>
                <div>
                  <h3 style="font-size:16px;font-weight:700;color:#1a1a2e;margin:0">{{ a.title }}</h3>
                  <p style="font-size:12px;color:#aaa;margin:2px 0 0">
                    {{ a.createdAt | date:'dd MMM yyyy, hh:mm a' }}
                    <span *ngIf="a.createdBy"> · by {{ a.createdBy.firstName }} {{ a.createdBy.lastName }}</span>
                  </p>
                </div>
              </div>
              <p style="color:#555;font-size:14px;line-height:1.6;margin:0 0 12px 50px">{{ a.content }}</p>
              <div style="margin-left:50px">
                <span class="status-chip" [class]="a.isActive ? 'APPROVED' : 'REJECTED'">
                  {{ a.isActive ? '✅ Active' : '⛔ Inactive' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </ng-container>
    </div>
  `,
  styles: [`
    .announcement-card { transition: box-shadow 0.2s; }
    .announcement-card:hover { box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
    .full-width { width: 100%; }
  `]
})
export class AdminAnnouncementsComponent implements OnInit {
  announcements: Announcement[] = [];
  showForm = false;
  saving = false;
  loading = true;
  form!: FormGroup;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Safety: force loading=false after 8s to prevent infinite spinner
    setTimeout(() => { this.loading = false; }, 8000); this.loadAnnouncements(); }

  loadAnnouncements(): void {
    this.loading = true;
    this.adminService.getAnnouncements().subscribe({
      next: (d: any) => {
        this.announcements = Array.isArray(d) ? d : (d?.data || d?.content || []);
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openForm(): void {
    this.form = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      isActive: [true]
    });
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.saving = true;
    this.adminService.createAnnouncement(this.form.value).subscribe({
      next: () => {
        this.saving = false;
        this.closeForm();
        this.loadAnnouncements();
        this.snackBar.open('Announcement published!', 'Close', { duration: 2000 });
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Error publishing!', 'Close', { duration: 2000 });
      }
    });
  }
}