import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { API_ROUTES, STORAGE_KEYS, ROLES } from '../constants/api.constants';
import { AuthResponse } from '../models/models';


@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = environment.apiUrl;

  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(
    this.getStoredUser()
  );
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<AuthResponse> {
  return this.http
    .post<any>(`${this.apiUrl}${API_ROUTES.AUTH.LOGIN}`, {
      email,
      password
    })
    .pipe(
      map(res => {
        const data = res.data || res;
        const authData: AuthResponse = {
          token: data.token,
          role: data.role,
          userId: data.userId,
          email: data.email
        };
        localStorage.setItem(STORAGE_KEYS.TOKEN, authData.token);
        localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(authData));
        this.currentUserSubject.next(authData);
        return authData;
      })
    );
}

  logout(): void {
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(STORAGE_KEYS.TOKEN);
  }

  getRole(): string | null {
  const role = this.getStoredUser()?.role || null;
  if (!role) return null;
  return role.replace('ROLE_', '').toUpperCase().trim();
  }

  getUserId(): number | null {
    return this.getStoredUser()?.userId || null;
  }

  getUserEmail(): string | null {
    return this.getStoredUser()?.email || null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    return this.getRole() === ROLES.ADMIN;
  }

  isManager(): boolean {
    return this.getRole() === ROLES.MANAGER;
  }

  isEmployee(): boolean {
    return this.getRole() === ROLES.EMPLOYEE;
  }

  private getStoredUser(): AuthResponse | null {
    const u = localStorage.getItem(STORAGE_KEYS.USER);
    return u ? JSON.parse(u) : null;
  }

  getCurrentUser(): AuthResponse | null {
  return this.getStoredUser();
  }
}