import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { AdminService } from '../../../core/services/admin.service';
import { LeaveApplication, LeaveType } from '../../../core/models/models';

@Component({
  selector: 'app-admin-leaves',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatTableModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatInputModule, MatCardModule,
    MatSnackBarModule, MatProgressSpinnerModule, MatTabsModule,
    MatTooltipModule, MatCheckboxModule
  ],
  template: `
    <div class="page-container">
      <div class="page-header"><h1>Leave Management</h1></div>

      <mat-tab-group>

        <!-- TAB 1: All Applications -->
        <mat-tab label="All Applications">
          <div style="margin-top:16px">
            <div *ngIf="loading" class="loading-overlay"><mat-spinner></mat-spinner></div>

            <div *ngIf="!loading && leaves.length === 0" class="empty-state">
              <mat-icon>event_available</mat-icon><p>No leave applications found</p>
            </div>

            <!-- Leave Cards -->
            <div *ngFor="let l of leaves" class="card" style="margin-bottom:14px;padding:20px">
              <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:12px">

                <!-- Left: Employee Info -->
                <div style="display:flex;align-items:center;gap:14px">
                  <div style="width:44px;height:44px;border-radius:50%;background:linear-gradient(135deg,#1a237e,#3949ab);
                    color:#fff;font-size:16px;font-weight:700;display:flex;align-items:center;justify-content:center;flex-shrink:0">
                    {{ (l.employeeName || 'U').charAt(0) }}
                  </div>
                  <div>
                    <div style="font-weight:700;color:#1a1a2e;font-size:15px">{{ l.employeeName || 'Unknown' }}</div>
                    <div style="font-size:12px;color:#888;margin-top:2px">
                      <span style="background:#e8eaf6;color:#3949ab;padding:2px 8px;border-radius:4px;font-weight:600">
                        {{ l.leaveTypeName || 'Leave' }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- Middle: Dates -->
                <div style="display:flex;gap:24px;align-items:center">
                  <div style="text-align:center">
                    <div style="font-size:11px;color:#aaa;margin-bottom:2px;text-transform:uppercase">From</div>
                    <div style="font-weight:700;color:#1a1a2e;font-size:14px">{{ l.startDate | date:'dd MMM yyyy' }}</div>
                  </div>
                  <mat-icon style="color:#ccc">arrow_forward</mat-icon>
                  <div style="text-align:center">
                    <div style="font-size:11px;color:#aaa;margin-bottom:2px;text-transform:uppercase">To</div>
                    <div style="font-weight:700;color:#1a1a2e;font-size:14px">{{ l.endDate | date:'dd MMM yyyy' }}</div>
                  </div>
                  <div style="text-align:center">
                    <div style="font-size:11px;color:#aaa;margin-bottom:2px;text-transform:uppercase">Days</div>
                    <div style="font-weight:700;color:#3949ab;font-size:14px">{{ l.numberOfDays || '-' }}</div>
                  </div>
                </div>

                <!-- Right: Status + Actions -->
                <div style="display:flex;align-items:center;gap:12px">
                  <span class="status-chip" [class]="l.status">{{ l.status }}</span>
                  <ng-container *ngIf="l.status === 'PENDING'">
                    <button mat-icon-button style="color:#2e7d32;background:#e8f5e9;border-radius:8px"
                      (click)="approveLeave(l.id)" matTooltip="Approve">
                      <mat-icon>check_circle</mat-icon>
                    </button>
                    <button mat-icon-button style="color:#c62828;background:#fce4ec;border-radius:8px"
                      (click)="rejectLeave(l.id)" matTooltip="Reject">
                      <mat-icon>cancel</mat-icon>
                    </button>
                  </ng-container>
                  <span *ngIf="l.status !== 'PENDING'" style="font-size:12px;color:#aaa;font-style:italic">
                    {{ l.status === 'APPROVED' ? '✅ Done' : l.status === 'REJECTED' ? '❌ Done' : '' }}
                  </span>
                </div>
              </div>

              <!-- Reason + Approver -->
              <div *ngIf="l.reason || l.approverName || l.approvalComment"
                style="margin-top:14px;padding-top:14px;border-top:1px solid #f0f0f0;display:flex;gap:24px;flex-wrap:wrap">
                <div *ngIf="l.reason" style="flex:1;min-width:200px">
                  <span style="font-size:11px;color:#aaa;text-transform:uppercase;font-weight:600">Reason</span>
                  <p style="font-size:13px;color:#555;margin:4px 0 0">{{ l.reason }}</p>
                </div>
                <div *ngIf="l.approverName" style="min-width:160px">
                  <span style="font-size:11px;color:#aaa;text-transform:uppercase;font-weight:600">Reviewed by</span>
                  <p style="font-size:13px;color:#555;margin:4px 0 0">{{ l.approverName }}</p>
                </div>
                <div *ngIf="l.approvalComment" style="flex:1;min-width:200px">
                  <span style="font-size:11px;color:#aaa;text-transform:uppercase;font-weight:600">Comment</span>
                  <p style="font-size:13px;color:#555;margin:4px 0 0">{{ l.approvalComment }}</p>
                </div>
              </div>
            </div>
          </div>
        </mat-tab>

        <!-- TAB 2: Leave Types -->
        <mat-tab label="Leave Types">
          <div style="margin-top:16px">
            <div style="margin-bottom:16px;display:flex;justify-content:flex-end">
              <button mat-flat-button color="primary" (click)="openTypeForm()">
                <mat-icon>add</mat-icon> Add Leave Type
              </button>
            </div>

            <div *ngIf="leaveTypes.length === 0" class="empty-state">
              <mat-icon>event_note</mat-icon><p>No leave types found</p>
            </div>

            <div style="display:grid;grid-template-columns:repeat(auto-fill,minmax(280px,1fr));gap:16px">
              <div *ngFor="let t of leaveTypes" class="card" style="padding:20px">
                <div style="display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:12px">
                  <div style="display:flex;align-items:center;gap:10px">
                    <div style="width:40px;height:40px;border-radius:10px;
                      background:linear-gradient(135deg,#4a148c,#7b1fa2);
                      display:flex;align-items:center;justify-content:center">
                      <mat-icon style="color:#fff;font-size:20px;width:20px;height:20px">event_note</mat-icon>
                    </div>
                    <div>
                      <div style="font-weight:700;color:#1a1a2e;font-size:15px">{{ t.name }}</div>
                      <span class="status-chip" [class]="t.isActive ? 'APPROVED' : 'REJECTED'" style="font-size:11px">
                        {{ t.isActive ? 'Active' : 'Inactive' }}
                      </span>
                    </div>
                  </div>
                  <button mat-icon-button color="primary" (click)="openTypeForm(t)" matTooltip="Edit">
                    <mat-icon>edit</mat-icon>
                  </button>
                </div>
                <div *ngIf="t.description" style="font-size:13px;color:#777;margin-bottom:10px">{{ t.description }}</div>
                <div style="display:flex;align-items:center;gap:6px;padding:8px 12px;background:#f3e5f5;border-radius:8px">
                  <mat-icon style="font-size:16px;width:16px;height:16px;color:#7b1fa2">calendar_month</mat-icon>
                  <span style="font-size:13px;font-weight:600;color:#4a148c">{{ t.defaultQuota }} days quota</span>
                </div>
              </div>
            </div>
          </div>
        </mat-tab>

      </mat-tab-group>

      <!-- Leave Type Form Dialog -->
      <div *ngIf="showTypeForm" class="dialog-overlay" (click)="closeTypeForm()">
        <mat-card class="dialog-card" style="max-width:460px;width:100%" (click)="$event.stopPropagation()">
          <mat-card-header style="padding:16px 16px 0">
            <mat-card-title>{{ typeEditId ? 'Edit' : 'Add' }} Leave Type</mat-card-title>
          </mat-card-header>
          <mat-card-content style="padding:16px !important">
            <form [formGroup]="typeForm" (ngSubmit)="onTypeSubmit()">
              <mat-form-field appearance="outline" style="width:100%">
                <mat-label>Name</mat-label>
                <input matInput formControlName="name">
              </mat-form-field>
              <mat-form-field appearance="outline" style="width:100%">
                <mat-label>Description</mat-label>
                <input matInput formControlName="description">
              </mat-form-field>
              <mat-form-field appearance="outline" style="width:100%">
                <mat-label>Default Quota (days)</mat-label>
                <input matInput type="number" formControlName="defaultQuota">
              </mat-form-field>
              <mat-checkbox formControlName="isActive" style="margin-bottom:16px;display:block">Active</mat-checkbox>
              <div style="display:flex;gap:12px;justify-content:flex-end;margin-top:8px">
                <button mat-button type="button" (click)="closeTypeForm()">Cancel</button>
                <button mat-flat-button color="primary" type="submit" [disabled]="typeForm.invalid || typeSaving">
                  {{ typeSaving ? 'Saving...' : 'Save' }}
                </button>
              </div>
            </form>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `
})
export class AdminLeavesComponent implements OnInit {
  leaves: LeaveApplication[] = [];
  leaveTypes: LeaveType[] = [];
  loading = true;
  showTypeForm = false;
  typeEditId: number | null = null;
  typeSaving = false;
  typeForm!: FormGroup;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Safety: force loading=false after 8s to prevent infinite spinner
    setTimeout(() => { this.loading = false; }, 8000);
    this.loadLeaves();
    this.loadLeaveTypes();
  }

  loadLeaves(): void {
    this.loading = true;
    this.adminService.getAllLeaves().subscribe({
      next: d => { this.leaves = d; this.loading = false; },
      error: () => this.loading = false
    });
  }

  loadLeaveTypes(): void {
    this.adminService.getLeaveTypes().subscribe({ next: d => this.leaveTypes = d });
  }

  approveLeave(id: number): void {
    this.adminService.approveLeave(id, 'Approved by Admin').subscribe({
      next: () => { this.snackBar.open('Leave approved!', 'Close', { duration: 2000 }); this.loadLeaves(); },
      error: () => this.snackBar.open('Error approving', 'Close', { duration: 2000 })
    });
  }

  rejectLeave(id: number): void {
    this.adminService.rejectLeave(id, 'Rejected by Admin').subscribe({
      next: () => { this.snackBar.open('Leave rejected', 'Close', { duration: 2000 }); this.loadLeaves(); },
      error: () => this.snackBar.open('Error rejecting', 'Close', { duration: 2000 })
    });
  }

  openTypeForm(t?: LeaveType): void {
    this.typeEditId = t?.id || null;
    this.typeForm = this.fb.group({
      name: [t?.name || '', Validators.required],
      description: [t?.description || ''],
      defaultQuota: [t?.defaultQuota || 12, [Validators.required, Validators.min(1)]],
      isActive: [t?.isActive !== false]
    });
    this.showTypeForm = true;
  }

  closeTypeForm(): void { this.showTypeForm = false; this.typeSaving = false; }

  onTypeSubmit(): void {
    if (this.typeForm.invalid) return;
    this.typeSaving = true;
    const obs = this.typeEditId
      ? this.adminService.updateLeaveType(this.typeEditId, this.typeForm.value)
      : this.adminService.createLeaveType(this.typeForm.value);
    obs.subscribe({
      next: () => {
        this.typeSaving = false;
        this.closeTypeForm();
        this.loadLeaveTypes();
        this.snackBar.open('Saved!', 'Close', { duration: 2000 });
      },
      error: () => { this.typeSaving = false; this.snackBar.open('Error saving', 'Close', { duration: 2000 }); }
    });
  }
}