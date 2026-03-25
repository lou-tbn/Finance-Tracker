import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Dashboard } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/dashboard`;

  getDashboard(userId: string, month?: number, year?: number): Observable<Dashboard> {
    let params = new HttpParams();
    if (month != null) {
      params = params.set('month', `${month}`);
    }
    if (year != null) {
      params = params.set('year', `${year}`);
    }
    return this.http.get<Dashboard>(`${this.baseUrl}/${userId}`, { params });
  }
}

