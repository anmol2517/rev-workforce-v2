import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { EmployeeService } from '../../../core/services/employee.service';

@Component({
  selector: 'app-employee-directory',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule,
    MatProgressSpinnerModule, MatFormFieldModule, MatInputModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1>Employee Directory</h1>
        <span style="background:#e3f2fd;color:#1565c0;padding:6px 14px;border-radius:20px;font-size:13px;font-weight:600">
          {{ employees.length }} Employees
        </span>
      </div>

      <div class="card" style="padding:20px;margin-bottom:20px">
        <mat-form-field appearance="outline" style="width:100%;max-width:400px;margin-bottom:-1.25em">
          <mat-label>Search by name, ID, department...</mat-label>
          <input matInput [(ngModel)]="searchQuery" (input)="onSearch()" placeholder="Type to search...">
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
      </div>

      <div *ngIf="loading" class="loading-overlay"><mat-spinner></mat-spinner></div>

      <div *ngIf="!loading && employees.length === 0" class="empty-state">
        <mat-icon>contacts</mat-icon><p>No employees found</p>
      </div>

      <div style="display:grid;grid-template-columns:repeat(auto-fill,minmax(260px,1fr));gap:16px">
        <div *ngFor="let e of employees" class="card"
          style="padding:18px;border-top:4px solid #4a148c">
          <div style="display:flex;align-items:center;gap:12px;margin-bottom:14px">
            <div style="width:46px;height:46px;border-radius:50%;
              background:linear-gradient(135deg,#4a148c,#6a1b9a);
              color:#fff;font-size:16px;font-weight:700;
              display:flex;align-items:center;justify-content:center;flex-shrink:0">
              {{ (e.firstName||'?').charAt(0) }}{{ (e.lastName||'').charAt(0) }}
            </div>
            <div>
              <div style="font-weight:700;color:#1a1a2e;font-size:14px">
                {{ e.firstName }} {{ e.lastName }}
              </div>
              <div style="font-size:12px;color:#888">{{ e.employeeId }}</div>
            </div>
          </div>

          <div style="display:flex;flex-direction:column;gap:6px">
            <div style="display:flex;align-items:center;gap:6px;font-size:13px;color:#555">
              <mat-icon style="font-size:15px;width:15px;height:15px;color:#888">badge</mat-icon>
              {{ e.designationName || e.designation?.name || '—' }}
            </div>
            <div style="display:flex;align-items:center;gap:6px;font-size:13px;color:#555">
              <mat-icon style="font-size:15px;width:15px;height:15px;color:#888">business</mat-icon>
              {{ e.departmentName || e.department?.name || '—' }}
            </div>
            <div style="display:flex;align-items:center;gap:6px;font-size:13px;color:#555;word-break:break-all">
              <mat-icon style="font-size:15px;width:15px;height:15px;color:#888">email</mat-icon>
              {{ e.email }}
            </div>
            <div *ngIf="e.phoneNumber" style="display:flex;align-items:center;gap:6px;font-size:13px;color:#555">
              <mat-icon style="font-size:15px;width:15px;height:15px;color:#888">phone</mat-icon>
              {{ e.phoneNumber }}
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class EmployeeDirectoryComponent implements OnInit {
  employees: any[] = [];
  allEmployees: any[] = [];
  loading = true;
  searchQuery = '';

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    // Safety: force loading=false after 8s to prevent infinite spinner
    setTimeout(() => { this.loading = false; }, 8000);
    this.employeeService.getDirectory().subscribe({
      next: (d: any) => {
        this.allEmployees = Array.isArray(d) ? d : (d?.data || []);
        this.employees = [...this.allEmployees];
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  onSearch(): void {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) {
      this.employees = [...this.allEmployees];
      return;
    }
    this.employees = this.allEmployees.filter(e =>
      `${e.firstName} ${e.lastName}`.toLowerCase().includes(q) ||
      (e.employeeId || '').toLowerCase().includes(q) ||
      (e.departmentName || e.department?.name || '').toLowerCase().includes(q) ||
      (e.designationName || e.designation?.name || '').toLowerCase().includes(q) ||
      (e.email || '').toLowerCase().includes(q)
    );
  }
}