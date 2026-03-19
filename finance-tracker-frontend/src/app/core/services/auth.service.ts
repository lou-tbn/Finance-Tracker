import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map, tap } from 'rxjs';
import { User } from '../models/user.model';
import { UserService } from './user.service';

const AUTH_USER_KEY = 'finance_tracker_active_user';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  login(email: string, password: string): Observable<User | null> {
    return this.userService.getAll().pipe(
      map((users) => {
        const normalizedEmail = email.trim().toLowerCase();
        return (
          users.find(
            (u) =>
              u.email.trim().toLowerCase() === normalizedEmail && u.password === password,
          ) ?? null
        );
      }),
      tap((user) => {
        if (user) {
          localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
        }
      }),
    );
  }

  getCurrentUser(): User | null {
    const raw = localStorage.getItem(AUTH_USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }

  getCurrentUserId(): string | null {
    return this.getCurrentUser()?.id ?? null;
  }

  isAuthenticated(): boolean {
    return !!this.getCurrentUser();
  }

  logout(): void {
    localStorage.removeItem(AUTH_USER_KEY);
    this.router.navigate(['/login']);
  }
}
