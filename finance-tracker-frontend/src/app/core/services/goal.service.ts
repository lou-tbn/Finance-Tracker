import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Goal, GoalRequest } from '../models/goal.model';

@Injectable({
  providedIn: 'root',
})
export class GoalService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/goals`;

  getAll(userId?: string, title?: string): Observable<Goal[]> {
    let params = new HttpParams();
    if (userId?.trim()) {
      params = params.set('userId', userId.trim());
    }
    if (title?.trim()) {
      params = params.set('title', title.trim());
    }
    return this.http.get<Goal[]>(this.baseUrl, { params });
  }

  create(payload: GoalRequest): Observable<Goal> {
    return this.http.post<Goal>(this.baseUrl, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
