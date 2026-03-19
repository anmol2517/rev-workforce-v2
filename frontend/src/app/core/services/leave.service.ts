import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { API_ROUTES } from '../constants/api.constants';
import { LeaveType, LeaveApplication, Holiday } from '../models/models';

@Injectable({ providedIn: 'root' })
export class LeaveService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ── TYPES ──────────────────────────────────────────────────
  getTypes(): Observable<LeaveType[]> {
    return this.http.get<LeaveType[]>(`${this.apiUrl}${API_ROUTES.LEAVES.TYPES}`);
  }

  getActiveTypes(): Observable<LeaveType[]> {
    return this.http.get<LeaveType[]>(`${this.apiUrl}${API_ROUTES.LEAVES.TYPES_ACTIVE}`);
  }

  // ── BALANCE ────────────────────────────────────────────────
  getBalance(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.LEAVES.BALANCE(userId)}`);
  }

  assignBalance(userId: number, leaveTypeId: number, quota: number): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('leaveTypeId', leaveTypeId.toString())
      .set('quota', quota.toString());
    return this.http.post(`${this.apiUrl}${API_ROUTES.LEAVES.BALANCE_ASSIGN}`, {}, { params });
  }

  adjustBalance(userId: number, leaveTypeId: number, adjustmentDays: number, reason: string): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('leaveTypeId', leaveTypeId.toString())
      .set('adjustmentDays', adjustmentDays.toString())
      .set('reason', reason);
    return this.http.post(`${this.apiUrl}${API_ROUTES.LEAVES.BALANCE_ADJUST}`, {}, { params });
  }

  bulkAssign(leaveTypeId: number, quota: number): Observable<any> {
    const params = new HttpParams()
      .set('leaveTypeId', leaveTypeId.toString())
      .set('quota', quota.toString());
    return this.http.post(`${this.apiUrl}${API_ROUTES.LEAVES.BULK_ASSIGN}`, {}, { params });
  }

  // ── APPLICATIONS ───────────────────────────────────────────
  getApplications(userId: number): Observable<LeaveApplication[]> {
    return this.http.get<LeaveApplication[]>(`${this.apiUrl}${API_ROUTES.LEAVES.APPLICATIONS(userId)}`);
  }

  getPendingTeam(): Observable<LeaveApplication[]> {
    return this.http.get<LeaveApplication[]>(`${this.apiUrl}${API_ROUTES.LEAVES.PENDING_TEAM}`);
  }

  applyLeave(data: any): Observable<LeaveApplication> {
    return this.http.post<LeaveApplication>(`${this.apiUrl}${API_ROUTES.LEAVES.APPLY}`, data);
  }

  approveLeave(id: number, comment: string): Observable<any> {
    const params = new HttpParams().set('comment', comment);
    return this.http.put(`${this.apiUrl}${API_ROUTES.LEAVES.APPROVE(id)}`, {}, { params });
  }

  rejectLeave(id: number, comment: string): Observable<any> {
    const params = new HttpParams().set('comment', comment);
    return this.http.put(`${this.apiUrl}${API_ROUTES.LEAVES.REJECT(id)}`, {}, { params });
  }

  cancelLeave(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}${API_ROUTES.LEAVES.CANCEL(id)}`);
  }

  // ── HOLIDAYS ───────────────────────────────────────────────
  getHolidays(): Observable<Holiday[]> {
    return this.http.get<Holiday[]>(`${this.apiUrl}${API_ROUTES.LEAVES.HOLIDAYS}`);
  }
}