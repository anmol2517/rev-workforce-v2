import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { API_ROUTES } from '../constants/api.constants';
import { User, LeaveApplication, PerformanceReview, Goal, Announcement, Notification } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ManagerService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ── PROFILE ────────────────────────────────────────────────
  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}${API_ROUTES.MANAGER.PROFILE}`);
  }

  updateProfile(data: any): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}${API_ROUTES.MANAGER.PROFILE}`, data);
  }

  // ── DASHBOARD ──────────────────────────────────────────────
  getDashboardStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.MANAGER.DASHBOARD}`);
  }

  // ── TEAM ───────────────────────────────────────────────────
  getTeamMembers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}${API_ROUTES.MANAGER.TEAM}`);
  }

  getTeamMember(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}${API_ROUTES.MANAGER.TEAM_MEMBER(id)}`);
  }

  // ── LEAVES ─────────────────────────────────────────────────
  getLeaveApplications(): Observable<LeaveApplication[]> {
    return this.http.get<LeaveApplication[]>(`${this.apiUrl}${API_ROUTES.MANAGER.LEAVES}`);
  }

  getLeaveApplication(id: number): Observable<LeaveApplication> {
    return this.http.get<LeaveApplication>(`${this.apiUrl}${API_ROUTES.MANAGER.LEAVE_BY_ID(id)}`);
  }

  getTeamCalendar(): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.MANAGER.TEAM_CALENDAR}`);
  }

  getTeamBalance(): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.MANAGER.TEAM_BALANCE}`);
  }

  approveLeave(id: number, comments: string): Observable<any> {
    const params = new HttpParams().set('comments', comments);
    return this.http.put(
      `${this.apiUrl}${API_ROUTES.MANAGER.LEAVE_APPROVE(id)}`,
      {},
      { params }
    );
  }

  rejectLeave(id: number, comments: string): Observable<any> {
    const params = new HttpParams().set('comments', comments);
    return this.http.put(
      `${this.apiUrl}${API_ROUTES.MANAGER.LEAVE_REJECT(id)}`,
      {},
      { params }
    );
  }

  // ── PERFORMANCE REVIEWS ────────────────────────────────────
  getPerformanceReviews(): Observable<PerformanceReview[]> {
    return this.http.get<PerformanceReview[]>(`${this.apiUrl}${API_ROUTES.MANAGER.REVIEWS}`);
  }

  getPerformanceReview(id: number): Observable<PerformanceReview> {
    return this.http.get<PerformanceReview>(`${this.apiUrl}${API_ROUTES.MANAGER.REVIEW_BY_ID(id)}`);
  }

  getReviewFeedback(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}${API_ROUTES.MANAGER.REVIEW_FEEDBACK(id)}`);
  }

  submitFeedback(id: number, data: any): Observable<any> {
    return this.http.post(
      `${this.apiUrl}${API_ROUTES.MANAGER.REVIEW_FEEDBACK(id)}`,
      data
    );
  }

  // ── GOALS ──────────────────────────────────────────────────
  getGoals(): Observable<Goal[]> {
    return this.http.get<Goal[]>(`${this.apiUrl}${API_ROUTES.MANAGER.GOALS}`);
  }

  getGoal(id: number): Observable<Goal> {
    return this.http.get<Goal>(`${this.apiUrl}${API_ROUTES.MANAGER.GOAL_BY_ID(id)}`);
  }

  createGoal(data: any): Observable<Goal> {
    return this.http.post<Goal>(`${this.apiUrl}${API_ROUTES.MANAGER.GOALS}`, data);
  }

  addGoalComment(id: number, comments: string): Observable<any> {
    const params = new HttpParams().set('comments', comments);
    return this.http.put(
      `${this.apiUrl}${API_ROUTES.MANAGER.GOAL_COMMENT(id)}`,
      {},
      { params }
    );
  }

  // ── NOTIFICATIONS ──────────────────────────────────────────
  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}${API_ROUTES.MANAGER.NOTIFICATIONS}`);
  }

  // ── ANNOUNCEMENTS ──────────────────────────────────────────
  createAnnouncement(data: any): Observable<Announcement> {
    return this.http.post<Announcement>(
      `${this.apiUrl}${API_ROUTES.MANAGER.ANNOUNCEMENTS}`,
      data
    );
  }

  getAnnouncements(): Observable<Announcement[]> {
  return this.http.get<Announcement[]>(`${this.apiUrl}/api/employee/announcements`);
  }
}