import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { API_ROUTES } from '../constants/api.constants';
import { User, LeaveApplication, LeaveBalance, LeaveType, PerformanceReview, Goal, Announcement, Notification, Holiday } from '../models/models';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ── DASHBOARD ──────────────────────────────────────────────
  getDashboard(): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.EMPLOYEE.DASHBOARD}`);
  }

  // ── PROFILE ────────────────────────────────────────────────
  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.PROFILE}`);
  }

  updateProfile(data: any): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.PROFILE}`, data);
  }

  getManager(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.MANAGER}`);
  }

  // ── LEAVES ─────────────────────────────────────────────────
  getLeaveBalance(): Observable<LeaveBalance[]> {
    return this.http.get<LeaveBalance[]>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.LEAVE_BALANCE}`);
  }

  getLeaveApplications(): Observable<LeaveApplication[]> {
    return this.http.get<LeaveApplication[]>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.LEAVE_APPLICATIONS}`);
  }

  getLeaveApplication(id: number): Observable<LeaveApplication> {
    return this.http.get<LeaveApplication>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.LEAVE_BY_ID(id)}`);
  }

  applyLeave(data: any): Observable<LeaveApplication> {
    return this.http.post<LeaveApplication>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.LEAVE_APPLY}`, data);
  }

  cancelLeave(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}${API_ROUTES.EMPLOYEE.LEAVE_CANCEL(id)}`);
  }

  getHolidays(): Observable<Holiday[]> {
    return this.http.get<Holiday[]>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.HOLIDAYS}`);
  }

  // ── PERFORMANCE REVIEWS ────────────────────────────────────
  getPerformanceReviews(): Observable<PerformanceReview[]> {
    return this.http.get<PerformanceReview[]>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.REVIEWS}`);
  }

  getPerformanceReview(id: number): Observable<PerformanceReview> {
    return this.http.get<PerformanceReview>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.REVIEW_BY_ID(id)}`);
  }

  getReviewFeedback(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.EMPLOYEE.REVIEW_FEEDBACK(id)}`);
  }

  createPerformanceReview(data: any): Observable<PerformanceReview> {
    return this.http.post<PerformanceReview>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.REVIEWS}`, data);
  }

  // ── GOALS ──────────────────────────────────────────────────
  getGoals(): Observable<Goal[]> {
    return this.http.get<Goal[]>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.GOALS}`);
  }

  getGoal(id: number): Observable<Goal> {
    return this.http.get<Goal>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.GOAL_BY_ID(id)}`);
  }

  createGoal(data: any): Observable<Goal> {
    return this.http.post<Goal>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.GOALS}`, data);
  }

  updateGoalProgress(id: number, progressPercentage: number, status: string): Observable<any> {
    const params = new HttpParams()
      .set('progressPercentage', progressPercentage.toString())
      .set('status', status);
    return this.http.put(
      `${this.apiUrl}${API_ROUTES.EMPLOYEE.GOAL_PROGRESS(id)}`,
      {},
      { params }
    );
  }

  // ── ANNOUNCEMENTS ──────────────────────────────────────────
  getAnnouncements(): Observable<Announcement[]> {
    return this.http.get<Announcement[]>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.ANNOUNCEMENTS}`);
  }

  getAnnouncement(id: number): Observable<Announcement> {
    return this.http.get<Announcement>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.ANNOUNCEMENT_BY_ID(id)}`);
  }

  // ── DIRECTORY ──────────────────────────────────────────────
  getDirectory(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.DIRECTORY}`);
  }

  searchDirectory(search: string): Observable<User[]> {
    const params = new HttpParams().set('search', search);
    return this.http.get<User[]>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.DIRECTORY_SEARCH}`, { params });
  }

  // ── NOTIFICATIONS ──────────────────────────────────────────
  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}${API_ROUTES.EMPLOYEE.NOTIFICATIONS}`);
  }

  getUnreadCount(): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.EMPLOYEE.NOTIFICATIONS_UNREAD}`);
  }

  markNotificationRead(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}${API_ROUTES.EMPLOYEE.NOTIFICATION_READ(id)}`, {});
  }

  markAllNotificationsRead(): Observable<any> {
    return this.http.put(`${this.apiUrl}${API_ROUTES.EMPLOYEE.NOTIFICATIONS_ALL_READ}`, {});
  }

  // ── MISC ───────────────────────────────────────────────────
  getQrCode(): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.EMPLOYEE.QRCODE}`);
  }

  downloadReport(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.EMPLOYEE.REPORT}`, { responseType: 'blob' });
  }
}