import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { AdminService } from '../../../core/services/admin.service';
import { User, Department, Designation } from '../../../core/models/models';

@Component({
  selector: 'app-admin-view-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatSelectModule,
    MatCardModule,
  ],
  template: `
    <div class="page-container">
      <div class="page-header"><h1>View Employee Profile</h1></div>

      <!-- Search Bar -->
      <div class="card" style="margin-bottom:24px">
        <mat-form-field appearance="outline" style="width:100%;margin-bottom:-1.25em">
          <mat-label>Search by name, email or Employee ID</mat-label>
          <input
            matInput
            [(ngModel)]="searchQuery"
            (input)="onSearch()"
            placeholder="e.g. Arjun, EMP-001"
          />
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>

        <div
          *ngIf="searchResults.length > 0 && !selectedEmployee"
          style="margin-top:10px;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden"
        >
          <div
            *ngFor="let e of searchResults"
            (click)="selectEmployee(e)"
            style="padding:12px 16px;cursor:pointer;display:flex;align-items:center;gap:12px;border-bottom:1px solid #f0f0f0;background:#fff"
            onmouseover="this.style.background='#f5f5f5'"
            onmouseout="this.style.background='#fff'"
          >
            <div
              style="width:36px;height:36px;border-radius:50%;background:#1a237e;color:#fff;
              font-size:14px;font-weight:700;display:flex;align-items:center;justify-content:center"
            >
              {{ e.firstName.charAt(0) }}{{ e.lastName.charAt(0) }}
            </div>
            <div>
              <div style="font-weight:600;color:#1a1a2e">{{ e.firstName }} {{ e.lastName }}</div>
              <div style="font-size:12px;color:#888">
                {{ e.employeeId }} · {{ e.role }} · {{ e.departmentName || '-' }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="loading" class="loading-overlay"><mat-spinner></mat-spinner></div>

      <!-- VIEW MODE -->
      <div *ngIf="selectedEmployee && !loading && !editMode">
        <div style="display:flex;gap:12px;margin-bottom:20px">
          <button mat-stroked-button (click)="clearSelection()">
            <mat-icon>arrow_back</mat-icon> Back
          </button>
          <button mat-flat-button color="primary" (click)="openEdit()">
            <mat-icon>edit</mat-icon> Edit Profile
          </button>
        </div>

        <div style="display:grid;grid-template-columns:300px 1fr;gap:24px">
          <!-- Left -->
          <div class="card" style="text-align:center;padding:24px">
            <div
              style="width:88px;height:88px;border-radius:50%;
              background:linear-gradient(135deg,#1a237e,#3949ab);color:#fff;
              font-size:32px;font-weight:700;display:flex;align-items:center;
              justify-content:center;margin:0 auto 16px"
            >
              {{ selectedEmployee.firstName.charAt(0) }}{{ selectedEmployee.lastName.charAt(0) }}
            </div>
            <h2 style="font-size:18px;font-weight:700;color:#1a1a2e;margin-bottom:4px">
              {{ selectedEmployee.firstName }} {{ selectedEmployee.lastName }}
            </h2>
            <p style="color:#888;font-size:13px;margin-bottom:8px">
              {{ selectedEmployee.employeeId }}
            </p>
            <span
              class="status-chip"
              [class]="selectedEmployee.role"
              style="display:inline-block;margin-bottom:8px"
              >{{ selectedEmployee.role }}</span
            >
            <br />
            <span class="status-chip APPROVED" style="display:inline-block;margin-top:6px">{{
              selectedEmployee.active
            }}</span>

            <div
              style="margin-top:20px;text-align:left;border-top:1px solid #f0f0f0;padding-top:16px"
            >
              <p
                style="display:flex;align-items:center;gap:8px;color:#555;font-size:13px;margin:10px 0"
              >
                <mat-icon style="font-size:16px;width:16px;height:16px;flex-shrink:0"
                  >email</mat-icon
                >
                {{ selectedEmployee.email }}
              </p>
              <p
                style="display:flex;align-items:center;gap:8px;color:#555;font-size:13px;margin:10px 0"
              >
                <mat-icon style="font-size:16px;width:16px;height:16px;flex-shrink:0"
                  >business</mat-icon
                >
                {{ selectedEmployee.departmentName || 'N/A' }}
              </p>
              <p
                style="display:flex;align-items:center;gap:8px;color:#555;font-size:13px;margin:10px 0"
              >
                <mat-icon style="font-size:16px;width:16px;height:16px;flex-shrink:0"
                  >badge</mat-icon
                >
                {{ selectedEmployee.designationName || 'N/A' }}
              </p>
              <p
                *ngIf="selectedEmployee.phoneNumber"
                style="display:flex;align-items:center;gap:8px;color:#555;font-size:13px;margin:10px 0"
              >
                <mat-icon style="font-size:16px;width:16px;height:16px;flex-shrink:0"
                  >phone</mat-icon
                >
                {{ selectedEmployee.phoneNumber }}
              </p>
              <p
                *ngIf="selectedEmployee.address"
                style="display:flex;align-items:center;gap:8px;color:#555;font-size:13px;margin:10px 0"
              >
                <mat-icon style="font-size:16px;width:16px;height:16px;flex-shrink:0"
                  >location_on</mat-icon
                >
                {{ selectedEmployee.address }}
              </p>
              <p
                *ngIf="selectedEmployee.joiningDate"
                style="display:flex;align-items:center;gap:8px;color:#555;font-size:13px;margin:10px 0"
              >
                <mat-icon style="font-size:16px;width:16px;height:16px;flex-shrink:0"
                  >calendar_today</mat-icon
                >
                Joined: {{ selectedEmployee.joiningDate | date: 'dd MMM yyyy' }}
              </p>
              <p
                *ngIf="selectedEmployee.managerName"
                style="display:flex;align-items:center;gap:8px;color:#555;font-size:13px;margin:10px 0"
              >
                <mat-icon style="font-size:16px;width:16px;height:16px;flex-shrink:0"
                  >manage_accounts</mat-icon
                >
                Manager: {{ selectedEmployee.managerName }}
              </p>
            </div>

            <!-- Export & Reports — WORKING BUTTONS -->
            <div style="margin-top:20px;padding-top:16px;border-top:1px solid #f0f0f0">
              <p
                style="font-size:11px;color:#aaa;margin-bottom:10px;text-transform:uppercase;letter-spacing:0.5px;font-weight:600;text-align:left"
              >
                Export &amp; Reports
              </p>
              <div style="display:flex;flex-direction:column;gap:8px">
                <button
                  mat-stroked-button
                  (click)="downloadPdf()"
                  [disabled]="downloading"
                  style="border-color:#c62828;color:#c62828;justify-content:flex-start;gap:8px"
                >
                  <mat-icon>picture_as_pdf</mat-icon>
                  {{ downloading ? 'Downloading...' : 'Download PDF' }}
                </button>
                <button
                  mat-stroked-button
                  (click)="downloadExcel()"
                  [disabled]="downloading"
                  style="border-color:#2e7d32;color:#2e7d32;justify-content:flex-start;gap:8px"
                >
                  <mat-icon>table_view</mat-icon>
                  Download Excel
                </button>
              </div>
            </div>
          </div>

          <!-- Right -->
          <div style="display:flex;flex-direction:column;gap:20px">
            <!-- QR Code -->
            <div class="card" style="text-align:center;padding:24px">
              <h3 style="font-weight:700;color:#1a1a2e;margin-bottom:6px">Employee QR Code</h3>
              <p style="font-size:13px;color:#888;margin-bottom:20px">
                Scan to view {{ selectedEmployee.firstName }}'s profile
              </p>
              <div style="display:flex;justify-content:center;margin-bottom:14px">
                <canvas
                  #qrCanvas
                  style="border:1px solid #e0e0e0;border-radius:8px;padding:8px"
                ></canvas>
              </div>
              <p style="font-size:12px;color:#aaa;margin-bottom:14px">
                {{ selectedEmployee.employeeId }} · {{ selectedEmployee.email }}
              </p>
              <button mat-stroked-button (click)="downloadQr()">
                <mat-icon>download</mat-icon> Download QR
              </button>
            </div>

            <!-- Employment Details -->
            <div class="card" style="padding:24px">
              <h3 style="font-weight:700;color:#1a1a2e;margin-bottom:20px">Employment Details</h3>
              <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px">
                <div style="padding:14px;background:#f8f9fa;border-radius:10px">
                  <div style="font-size:11px;color:#888;margin-bottom:5px">Employee ID</div>
                  <div style="font-weight:700;color:#1a1a2e">{{ selectedEmployee.employeeId }}</div>
                </div>
                <div style="padding:14px;background:#f8f9fa;border-radius:10px">
                  <div style="font-size:11px;color:#888;margin-bottom:5px">Role</div>
                  <div style="font-weight:700;color:#1a1a2e">{{ selectedEmployee.role }}</div>
                </div>
                <div style="padding:14px;background:#f8f9fa;border-radius:10px">
                  <div style="font-size:11px;color:#888;margin-bottom:5px">Department</div>
                  <div style="font-weight:700;color:#1a1a2e">
                    {{ selectedEmployee.departmentName || 'N/A' }}
                  </div>
                </div>
                <div style="padding:14px;background:#f8f9fa;border-radius:10px">
                  <div style="font-size:11px;color:#888;margin-bottom:5px">Designation</div>
                  <div style="font-weight:700;color:#1a1a2e">
                    {{ selectedEmployee.designationName || 'N/A' }}
                  </div>
                </div>
                <div
                  *ngIf="selectedEmployee.salary"
                  style="padding:14px;background:#e8f5e9;border-radius:10px"
                >
                  <div style="font-size:11px;color:#388e3c;margin-bottom:5px">Salary</div>
                  <div style="font-weight:700;color:#2e7d32">
                    ₹{{ selectedEmployee.salary | number }}
                  </div>
                </div>
                <div style="padding:14px;background:#e8eaf6;border-radius:10px">
                  <div style="font-size:11px;color:#3949ab;margin-bottom:5px">Status</div>
                  <div style="font-weight:700;color:#1a237e">{{ selectedEmployee.active }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- EDIT MODE -->
      <div *ngIf="selectedEmployee && editMode && !loading">
        <div style="display:flex;gap:12px;margin-bottom:20px;align-items:center">
          <button mat-stroked-button (click)="cancelEdit()">
            <mat-icon>close</mat-icon> Cancel
          </button>
          <h2 style="font-size:16px;font-weight:700;color:#1a1a2e;margin:0">
            Editing: {{ selectedEmployee.firstName }} {{ selectedEmployee.lastName }}
          </h2>
        </div>

        <div class="card" style="padding:24px">
          <form [formGroup]="editForm" (ngSubmit)="saveEdit()">
            <div class="form-grid">
              <mat-form-field appearance="outline">
                <mat-label>First Name</mat-label>
                <input matInput formControlName="firstName" />
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Last Name</mat-label>
                <input matInput formControlName="lastName" />
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Email</mat-label>
                <input matInput formControlName="email" />
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
                  <mat-option *ngFor="let d of designations" [value]="d.id">{{
                    d.name
                  }}</mat-option>
                </mat-select>
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Phone Number</mat-label>
                <input matInput formControlName="phoneNumber" />
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Salary</mat-label>
                <input matInput type="number" formControlName="salary" />
              </mat-form-field>
              <mat-form-field appearance="outline" style="grid-column:span 2">
                <mat-label>Address</mat-label>
                <input matInput formControlName="address" />
              </mat-form-field>
            </div>
            <div style="display:flex;gap:12px;justify-content:flex-end;margin-top:16px">
              <button mat-button type="button" (click)="cancelEdit()">Cancel</button>
              <button
                mat-flat-button
                color="primary"
                type="submit"
                [disabled]="editForm.invalid || saving"
              >
                {{ saving ? 'Saving...' : 'Save Changes' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Empty State -->
      <div *ngIf="!selectedEmployee && !loading && searchQuery.length === 0" class="empty-state">
        <mat-icon>manage_search</mat-icon>
        <p>Search for an employee to view their profile</p>
      </div>
    </div>
  `,
})
export class AdminViewProfileComponent implements OnInit {
  @ViewChild('qrCanvas') qrCanvas!: ElementRef<HTMLCanvasElement>;

  allEmployees: User[] = [];
  searchResults: User[] = [];
  selectedEmployee: User | null = null;
  departments: Department[] = [];
  designations: Designation[] = [];
  editForm!: FormGroup;
  searchQuery = '';
  loading = false;
  saving = false;
  downloading = false;
  editMode = false;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.adminService.getEmployees().subscribe({ next: (d) => (this.allEmployees = d) });
    this.adminService.getDepartments().subscribe({ next: (d) => (this.departments = d) });
    this.adminService.getDesignations().subscribe({ next: (d) => (this.designations = d) });
  }

  onSearch(): void {
    const q = this.searchQuery.toLowerCase().trim();
    if (!q) {
      this.searchResults = [];
      return;
    }
    this.searchResults = this.allEmployees
      .filter(
        (e) =>
          e.firstName.toLowerCase().includes(q) ||
          e.lastName.toLowerCase().includes(q) ||
          e.email.toLowerCase().includes(q) ||
          e.employeeId.toLowerCase().includes(q) ||
          `${e.firstName} ${e.lastName}`.toLowerCase().includes(q),
      )
      .slice(0, 6);
  }

  selectEmployee(emp: User): void {
    this.selectedEmployee = emp;
    this.searchResults = [];
    this.searchQuery = `${emp.firstName} ${emp.lastName}`;
    this.editMode = false;
    setTimeout(() => this.generateQr(), 150);
  }

  clearSelection(): void {
    this.selectedEmployee = null;
    this.searchQuery = '';
    this.searchResults = [];
    this.editMode = false;
  }

  openEdit(): void {
    if (!this.selectedEmployee) return;
    const emp = this.selectedEmployee;
    this.editForm = this.fb.group({
      firstName: [emp.firstName, Validators.required],
      lastName: [emp.lastName, Validators.required],
      email: [emp.email, [Validators.required, Validators.email]],
      role: [emp.role, Validators.required],
      departmentId: [emp.departmentId || null],
      designationId: [emp.designationId || null],
      phoneNumber: [emp.phoneNumber || ''],
      salary: [emp.salary || ''],
      address: [emp.address || ''],
      joiningDate: [emp.joiningDate || ''],
    });
    this.editMode = true;
  }

  cancelEdit(): void {
    this.editMode = false;
  }

  saveEdit(): void {
    if (!this.selectedEmployee || this.editForm.invalid) return;
    this.saving = true;
    this.adminService.updateEmployee(this.selectedEmployee.id, this.editForm.value).subscribe({
      next: () => {
        const v = this.editForm.value;
        this.selectedEmployee = {
          ...this.selectedEmployee!,
          ...v,
          departmentName:
            this.departments.find((d) => d.id === v.departmentId)?.name ||
            this.selectedEmployee!.departmentName,
          designationName:
            this.designations.find((d) => d.id === v.designationId)?.name ||
            this.selectedEmployee!.designationName,
        };
        const idx = this.allEmployees.findIndex((e) => e.id === this.selectedEmployee!.id);
        if (idx > -1) this.allEmployees[idx] = this.selectedEmployee!;
        this.saving = false;
        this.editMode = false;
        this.snackBar.open('Profile updated!', 'Close', { duration: 2000 });
        setTimeout(() => this.generateQr(), 150);
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Error updating profile', 'Close', { duration: 2000 });
      },
    });
  }

  generateQr(): void {
    const canvas = this.qrCanvas?.nativeElement;
    if (!canvas || !this.selectedEmployee) return;
    const emp = this.selectedEmployee;
    const text = [
      `Name: ${emp.firstName} ${emp.lastName}`,
      `ID: ${emp.employeeId}`,
      `Email: ${emp.email}`,
      `Role: ${emp.role}`,
      `Dept: ${emp.departmentName || 'N/A'}`,
      `Designation: ${emp.designationName || 'N/A'}`,
      `Status: ${emp.active}`,
    ].join('\n');
    const size = 200;
    const ctx = canvas.getContext('2d');
    canvas.width = size;
    canvas.height = size;
    const img = new Image();
    img.crossOrigin = 'anonymous';
    img.onload = () => {
      ctx?.drawImage(img, 0, 0, size, size);
    };
    img.src = `https://api.qrserver.com/v1/create-qr-code/?size=${size}x${size}&data=${encodeURIComponent(text)}`;
  }

  downloadQr(): void {
    const canvas = this.qrCanvas?.nativeElement;
    if (!canvas) return;
    const link = document.createElement('a');
    link.download = `${this.selectedEmployee?.employeeId}-qrcode.png`;
    link.href = canvas.toDataURL();
    link.click();
  }

  downloadPdf(): void {
  if (!this.selectedEmployee) return;
  this.downloading = true;
  this.adminService.exportEmployeePdf(this.selectedEmployee.id).subscribe({
    next: (blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${this.selectedEmployee?.employeeId}-report.pdf`;
      a.click();
      window.URL.revokeObjectURL(url);
      this.downloading = false;
      this.snackBar.open('PDF downloaded!', 'Close', { duration: 2000 });
    },
    error: () => { this.downloading = false; this.snackBar.open('Error', 'Close', { duration: 2000 }); }
  });
}

downloadExcel(): void {
  this.downloading = true;
  this.adminService.exportExcel().subscribe({
    next: (blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `revworkforce-employees.xlsx`;
      a.click();
      window.URL.revokeObjectURL(url);
      this.downloading = false;
      this.snackBar.open('Excel downloaded!', 'Close', { duration: 2000 });
    },
    error: () => { this.downloading = false; this.snackBar.open('Error', 'Close', { duration: 2000 }); }
  });
}
}