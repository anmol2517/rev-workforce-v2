import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AdminService } from '../../../core/services/admin.service';
import { User, Department, Designation } from '../../../core/models/models';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatTableModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatCardModule, MatSnackBarModule, MatProgressSpinnerModule, MatTooltipModule
  ],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h1>Employees</h1>
        <button mat-flat-button color="primary" (click)="openForm()">
          <mat-icon>add</mat-icon> Add Employee
        </button>
      </div>

      <div class="card">
        <mat-form-field appearance="outline" style="width:300px;margin-bottom:8px">
          <mat-label>Search employees</mat-label>
          <input matInput #searchInput (input)="onSearch(searchInput.value)">
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>

        <div *ngIf="loading" class="loading-overlay"><mat-spinner></mat-spinner></div>

        <table mat-table [dataSource]="employees" *ngIf="!loading">
          <ng-container matColumnDef="employeeId">
            <th mat-header-cell *matHeaderCellDef>Emp ID</th>
            <td mat-cell *matCellDef="let e">{{ e.employeeId }}</td>
          </ng-container>
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Name</th>
            <td mat-cell *matCellDef="let e"><strong>{{ e.firstName }} {{ e.lastName }}</strong></td>
          </ng-container>
          <ng-container matColumnDef="email">
            <th mat-header-cell *matHeaderCellDef>Email</th>
            <td mat-cell *matCellDef="let e">{{ e.email }}</td>
          </ng-container>
          <ng-container matColumnDef="role">
            <th mat-header-cell *matHeaderCellDef>Role</th>
            <td mat-cell *matCellDef="let e">
              <span class="status-chip" [class]="e.role">{{ e.role }}</span>
            </td>
          </ng-container>
          <ng-container matColumnDef="department">
            <th mat-header-cell *matHeaderCellDef>Department</th>
            <td mat-cell *matCellDef="let e">{{ e.departmentName || '-' }}</td>
          </ng-container>
          <ng-container matColumnDef="designation">
            <th mat-header-cell *matHeaderCellDef>Designation</th>
            <td mat-cell *matCellDef="let e">{{ e.designationName || '-' }}</td>
          </ng-container>
          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef>Status</th>
            <td mat-cell *matCellDef="let e">
              <span class="status-chip" [class]="e.active">{{ e.active }}</span>
            </td>
          </ng-container>
          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef>Actions</th>
            <td mat-cell *matCellDef="let e">
              <button mat-icon-button color="primary" (click)="openForm(e)" matTooltip="Edit">
                <mat-icon>edit</mat-icon>
              </button>
              <button mat-icon-button
                [color]="e.active === 'Working' ? 'warn' : 'primary'"
                (click)="toggleActive(e)"
                [matTooltip]="e.active === 'Working' ? 'Deactivate' : 'Activate'">
                <mat-icon>{{ e.active === 'Working' ? 'person_off' : 'person' }}</mat-icon>
              </button>
              <button mat-icon-button color="warn" (click)="deleteEmployee(e.id)" matTooltip="Delete">
                <mat-icon>delete</mat-icon>
              </button>
            </td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
        </table>

        <div *ngIf="!loading && employees.length === 0" class="empty-state">
          <mat-icon>people_outline</mat-icon>
          <p>No employees found</p>
        </div>
      </div>

      <!-- Form Dialog -->
      <div *ngIf="showForm" class="dialog-overlay" (click)="closeForm()">
        <mat-card class="dialog-card" (click)="$event.stopPropagation()">
          <mat-card-header style="padding:16px 16px 0">
            <mat-card-title>{{ editId ? 'Edit Employee' : 'Add Employee' }}</mat-card-title>
          </mat-card-header>
          <mat-card-content style="padding:16px !important">
            <form [formGroup]="form" (ngSubmit)="onSubmit()">
              <div class="form-grid">
                <mat-form-field appearance="outline">
                  <mat-label>First Name</mat-label>
                  <input matInput formControlName="firstName">
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Last Name</mat-label>
                  <input matInput formControlName="lastName">
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Email</mat-label>
                  <input matInput formControlName="email">
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Role</mat-label>
                  <mat-select formControlName="role">
                    <mat-option value="EMPLOYEE">Employee</mat-option>
                    <mat-option value="MANAGER">Manager</mat-option>
                  </mat-select>
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Department</mat-label>
                  <mat-select formControlName="departmentId">
                    <mat-option *ngFor="let d of departments" [value]="d.id">{{ d.name }}</mat-option>
                  </mat-select>
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Designation</mat-label>
                  <mat-select formControlName="designationId">
                    <mat-option *ngFor="let d of designations" [value]="d.id">{{ d.name }}</mat-option>
                  </mat-select>
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Salary</mat-label>
                  <input matInput type="number" formControlName="salary">
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Phone Number</mat-label>
                  <input matInput formControlName="phoneNumber">
                </mat-form-field>
                <mat-form-field appearance="outline" style="grid-column: span 2">
                  <mat-label>Address</mat-label>
                  <input matInput formControlName="address">
                </mat-form-field>
              </div>
              <div style="display:flex;gap:12px;justify-content:flex-end;margin-top:12px">
                <button mat-button type="button" (click)="closeForm()">Cancel</button>
                <button mat-flat-button color="primary" type="submit"
                  [disabled]="form.invalid || saving">
                  {{ saving ? 'Saving...' : (editId ? 'Update' : 'Create') }}
                </button>
              </div>
            </form>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `
})
export class EmployeesComponent implements OnInit {
  employees: User[] = [];
  departments: Department[] = [];
  designations: Designation[] = [];
  displayedColumns = ['employeeId', 'name', 'email', 'role', 'department', 'designation', 'status', 'actions'];
  loading = true;
  showForm = false;
  saving = false;
  editId: number | null = null;
  form!: FormGroup;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Safety: force loading=false after 8s to prevent infinite spinner
    setTimeout(() => { this.loading = false; }, 8000);
    this.loadEmployees();
    this.adminService.getDepartments().subscribe(d => this.departments = d);
    this.adminService.getDesignations().subscribe(d => this.designations = d);
  }

  loadEmployees(): void {
    this.loading = true;
    this.adminService.getEmployees().subscribe({
      next: (data) => { this.employees = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  initForm(emp?: User): void {
  this.form = this.fb.group({
    firstName: [emp?.firstName || '', Validators.required],
    lastName: [emp?.lastName || '', Validators.required],
    email: [emp?.email || '', [Validators.required, Validators.email]],
    role: [emp?.role || 'EMPLOYEE', Validators.required],
    departmentId: [emp?.departmentId || '', Validators.required],
    designationId: [emp?.designationId || '', Validators.required],
    salary: [emp?.salary || ''],
    phoneNumber: [emp?.phoneNumber || ''],
    address: [emp?.address || ''],
    joiningDate: [emp?.joiningDate || new Date().toISOString()]
  });
}

  openForm(emp?: User): void {
    this.editId = emp?.id || null;
    this.initForm(emp);
    this.showForm = true;
  }

  closeForm(): void { this.showForm = false; this.editId = null; }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.saving = true;
    const req = this.editId
      ? this.adminService.updateEmployee(this.editId, this.form.value)
      : this.adminService.createEmployee(this.form.value);

    req.subscribe({
      next: () => {
        this.saving = false;
        this.closeForm();
        this.loadEmployees();
        this.snackBar.open('Employee saved successfully!', 'Close', { duration: 2000 });
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Error saving employee', 'Close', { duration: 3000 });
      }
    });
  }

  toggleActive(emp: User): void {
    const req = emp.active === 'Working'
      ? this.adminService.deactivateEmployee(emp.id)
      : this.adminService.activateEmployee(emp.id);
    req.subscribe({
      next: () => {
        this.loadEmployees();
        this.snackBar.open('Status updated!', 'Close', { duration: 2000 });
      }
    });
  }

  deleteEmployee(id: number): void {
    if (!confirm('Are you sure you want to delete this employee?')) return;
    this.adminService.deleteEmployee(id).subscribe({
      next: () => {
        this.loadEmployees();
        this.snackBar.open('Employee deleted!', 'Close', { duration: 2000 });
      }
    });
  }

  onSearch(query: string): void {
    if (!query.trim()) { this.loadEmployees(); return; }
    this.adminService.searchEmployees(query).subscribe({
      next: (data) => this.employees = data,
      error: () => {}
    });
  }
}