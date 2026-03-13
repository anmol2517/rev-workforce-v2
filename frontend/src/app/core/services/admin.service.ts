import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { API_ROUTES } from '../constants/api.constants';
import { User, Department, Designation, LeaveType, LeaveApplication, Holiday, Announcement, Goal } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ── DASHBOARD ──────────────────────────────────────────────
  getDashboardStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.ADMIN.DASHBOARD}`);
  }

  // ── EMPLOYEES ──────────────────────────────────────────────
  getEmployees(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEES}`);
  }

  getEmployee(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEE_BY_ID(id)}`);
  }

  searchEmployees(query: string): Observable<User[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<User[]>(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEE_SEARCH}`, { params });
  }

  createEmployee(data: any): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEES}`, data);
  }

  bulkCreateEmployees(data: any[]): Observable<User[]> {
    return this.http.post<User[]>(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEES}/bulk`, data);
  }

  updateEmployee(id: number, data: any): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEE_BY_ID(id)}`, data);
  }

  activateEmployee(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEE_ACTIVATE(id)}`, {});
  }

  deactivateEmployee(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEE_DEACTIVATE(id)}`, {});
  }

  assignManager(empId: number, managerId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEE_ASSIGN_MANAGER(empId, managerId)}`, {});
  }

  deleteEmployee(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}${API_ROUTES.ADMIN.EMPLOYEE_BY_ID(id)}`);
  }

  // ── DEPARTMENTS ────────────────────────────────────────────
  getDepartments(): Observable<Department[]> {
    return this.http.get<Department[]>(`${this.apiUrl}${API_ROUTES.ADMIN.DEPARTMENTS}`);
  }

  getDepartment(id: number): Observable<Department> {
    return this.http.get<Department>(`${this.apiUrl}${API_ROUTES.ADMIN.DEPARTMENT_BY_ID(id)}`);
  }

  createDepartment(data: any): Observable<Department> {
    return this.http.post<Department>(`${this.apiUrl}${API_ROUTES.ADMIN.DEPARTMENTS}`, data);
  }

  updateDepartment(id: number, data: any): Observable<Department> {
    return this.http.put<Department>(`${this.apiUrl}${API_ROUTES.ADMIN.DEPARTMENT_BY_ID(id)}`, data);
  }

  deleteDepartment(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}${API_ROUTES.ADMIN.DEPARTMENT_BY_ID(id)}`);
  }

  // ── DESIGNATIONS ───────────────────────────────────────────
  getDesignations(): Observable<Designation[]> {
    return this.http.get<Designation[]>(`${this.apiUrl}${API_ROUTES.ADMIN.DESIGNATIONS}`);
  }

  createDesignation(data: any): Observable<Designation> {
    return this.http.post<Designation>(`${this.apiUrl}${API_ROUTES.ADMIN.DESIGNATIONS}`, data);
  }

  updateDesignation(id: number, data: any): Observable<Designation> {
    return this.http.put<Designation>(`${this.apiUrl}${API_ROUTES.ADMIN.DESIGNATION_BY_ID(id)}`, data);
  }

  deleteDesignation(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}${API_ROUTES.ADMIN.DESIGNATION_BY_ID(id)}`);
  }

  // ── LEAVES ─────────────────────────────────────────────────
  getAllLeaves(): Observable<LeaveApplication[]> {
    return this.http.get<LeaveApplication[]>(`${this.apiUrl}${API_ROUTES.ADMIN.LEAVES_ALL}`);
  }

  getLeaveTypes(): Observable<LeaveType[]> {
    return this.http.get<LeaveType[]>(`${this.apiUrl}${API_ROUTES.ADMIN.LEAVES_TYPES}`);
  }

  getLeaveUtilization(): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.ADMIN.LEAVES_UTILIZATION}`);
  }

  createLeaveTypesBulk(data: any[]): Observable<any> {
    return this.http.post(`${this.apiUrl}${API_ROUTES.ADMIN.LEAVES_TYPES_BULK}`, data);
  }

  updateLeaveType(id: number, data: any): Observable<LeaveType> {
    return this.http.put<LeaveType>(`${this.apiUrl}${API_ROUTES.ADMIN.LEAVE_TYPE_BY_ID(id)}`, data);
  }

  approveLeave(id: number, comment: string): Observable<any> {
    const params = new HttpParams().set('comment', comment);
    return this.http.put(`${this.apiUrl}${API_ROUTES.ADMIN.LEAVE_APPROVE(id)}`, {}, { params });
  }

  rejectLeave(id: number, reason: string): Observable<any> {
    const params = new HttpParams().set('reason', reason);
    return this.http.put(`${this.apiUrl}${API_ROUTES.ADMIN.LEAVE_REJECT(id)}`, {}, { params });
  }

  // ── HOLIDAYS ───────────────────────────────────────────────
  getHolidays(): Observable<Holiday[]> {
    return this.http.get<Holiday[]>(`${this.apiUrl}${API_ROUTES.ADMIN.HOLIDAYS}`);
  }

  getHoliday(id: number): Observable<Holiday> {
    return this.http.get<Holiday>(`${this.apiUrl}${API_ROUTES.ADMIN.HOLIDAY_BY_ID(id)}`);
  }

  addHoliday(data: any): Observable<Holiday> {
    return this.http.post<Holiday>(`${this.apiUrl}${API_ROUTES.ADMIN.HOLIDAY_ADD}`, data);
  }

  bulkAddHolidays(data: any[]): Observable<any> {
    return this.http.post(`${this.apiUrl}${API_ROUTES.ADMIN.HOLIDAY_BULK_ADD}`, data);
  }

  updateHoliday(id: number, data: any): Observable<Holiday> {
    return this.http.put<Holiday>(`${this.apiUrl}${API_ROUTES.ADMIN.HOLIDAY_BY_ID(id)}`, data);
  }

  deleteHoliday(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}${API_ROUTES.ADMIN.HOLIDAY_BY_ID(id)}`);
  }

  // ── ANNOUNCEMENTS ──────────────────────────────────────────
  createAnnouncement(data: any): Observable<Announcement> {
    return this.http.post<Announcement>(`${this.apiUrl}${API_ROUTES.ADMIN.ANNOUNCEMENTS}`, data);
  }

getAnnouncements(): Observable<Announcement[]> {
  return this.http.get<Announcement[]>(`${this.apiUrl}/api/employee/announcements`);
}
 
  // ── REPORTS ────────────────────────────────────────────────
  exportExcel(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.ADMIN.REPORTS_EXCEL}`, { responseType: 'blob' });
  }

  exportEmployeePdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.ADMIN.REPORT_PDF(id)}`, { responseType: 'blob' });
  }

  createLeaveType(data: any): Observable<LeaveType> {
  return this.http.post<LeaveType>(`${this.apiUrl}${API_ROUTES.ADMIN.LEAVES_TYPES}`, data);
  }

  getAllGoals(): Observable<Goal[]> {
    return this.http.get<Goal[]>(`${this.apiUrl}/api/admin/goals`);
  }
}
