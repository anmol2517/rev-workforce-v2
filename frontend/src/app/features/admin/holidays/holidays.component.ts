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
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AdminService } from '../../../core/services/admin.service';
import { Holiday } from '../../../core/models/models';

@Component({
  selector: 'app-holidays',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatTableModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatInputModule, MatCardModule,
    MatSnackBarModule, MatProgressSpinnerModule, MatCheckboxModule, MatTooltipModule
  ],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1>Holidays</h1>
        <button mat-flat-button color="primary" (click)="openForm()">
          <mat-icon>add</mat-icon> Add Holiday
        </button>
      </div>
      <div class="card">
        <div *ngIf="loading" class="loading-overlay"><mat-spinner></mat-spinner></div>
        <table mat-table [dataSource]="holidays" *ngIf="!loading">
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Name</th>
            <td mat-cell *matCellDef="let h"><strong>{{ h.name }}</strong></td>
          </ng-container>
          <ng-container matColumnDef="date">
            <th mat-header-cell *matHeaderCellDef>Date</th>
            <td mat-cell *matCellDef="let h">{{ h.holidayDate | date:'dd MMM yyyy' }}</td>
          </ng-container>
          <ng-container matColumnDef="type">
            <th mat-header-cell *matHeaderCellDef>Type</th>
            <td mat-cell *matCellDef="let h">
              <span class="status-chip" [class]="h.isOptional ? 'PENDING' : 'APPROVED'">
                {{ h.isOptional ? 'Optional' : 'Mandatory' }}
              </span>
            </td>
          </ng-container>
          <ng-container matColumnDef="description">
            <th mat-header-cell *matHeaderCellDef>Description</th>
            <td mat-cell *matCellDef="let h">{{ h.description || '-' }}</td>
          </ng-container>
          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef>Actions</th>
            <td mat-cell *matCellDef="let h">
              <button mat-icon-button color="primary" (click)="openForm(h)" matTooltip="Edit">
                <mat-icon>edit</mat-icon>
              </button>
              <button mat-icon-button color="warn" (click)="delete(h.id)" matTooltip="Delete">
                <mat-icon>delete</mat-icon>
              </button>
            </td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="cols"></tr>
          <tr mat-row *matRowDef="let row; columns: cols;"></tr>
        </table>
        <div *ngIf="!loading && holidays.length === 0" class="empty-state">
          <mat-icon>celebration</mat-icon><p>No holidays added</p>
        </div>
      </div>

      <div *ngIf="showForm" class="dialog-overlay" (click)="closeForm()">
        <mat-card class="dialog-card" (click)="$event.stopPropagation()">
          <mat-card-header style="padding:16px 16px 0">
            <mat-card-title>{{ editId ? 'Edit' : 'Add' }} Holiday</mat-card-title>
          </mat-card-header>
          <mat-card-content style="padding:16px !important">
            <form [formGroup]="form" (ngSubmit)="onSubmit()">
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Holiday Name</mat-label>
                <input matInput formControlName="name">
              </mat-form-field>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Date</mat-label>
                <input matInput type="date" formControlName="holidayDate">
              </mat-form-field>
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>Description</mat-label>
                <input matInput formControlName="description">
              </mat-form-field>
              <mat-checkbox formControlName="isOptional" style="margin-bottom:16px">
                Optional Holiday
              </mat-checkbox>
              <div style="display:flex;gap:12px;justify-content:flex-end;margin-top:8px">
                <button mat-button type="button" (click)="closeForm()">Cancel</button>
                <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid">
                  {{ editId ? 'Update' : 'Create' }}
                </button>
              </div>
            </form>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `
})
export class HolidaysComponent implements OnInit {
  holidays: Holiday[] = [];
  cols = ['name', 'date', 'type', 'description', 'actions'];
  loading = true; showForm = false; editId: number | null = null;
  form!: FormGroup;

  constructor(private adminService: AdminService, private fb: FormBuilder, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    // Safety: force loading=false after 8s to prevent infinite spinner
    setTimeout(() => { this.loading = false; }, 8000); this.load(); }

  load(): void {
    this.loading = true;
    this.adminService.getHolidays().subscribe({
      next: d => { this.holidays = d; this.loading = false; },
      error: () => this.loading = false
    });
  }

  openForm(h?: Holiday): void {
    this.editId = h?.id || null;
    this.form = this.fb.group({
      name: [h?.name || '', Validators.required],
      holidayDate: [h?.holidayDate?.split('T')[0] || '', Validators.required],
      description: [h?.description || ''],
      isOptional: [h?.isOptional || false]
    });
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; }

  onSubmit(): void {
    const req = this.editId
      ? this.adminService.updateHoliday(this.editId, this.form.value)
      : this.adminService.addHoliday(this.form.value);
    req.subscribe({
      next: () => { this.closeForm(); this.load(); this.snackBar.open('Saved!', 'Close', { duration: 2000 }); },
      error: () => this.snackBar.open('Error!', 'Close', { duration: 2000 })
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this holiday?')) return;
    this.adminService.deleteHoliday(id).subscribe({
      next: () => { this.load(); this.snackBar.open('Deleted!', 'Close', { duration: 2000 }); }
    });
  }
}