import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/users`;

  getAll(username?: string): Observable<User[]> {
    let params = new HttpParams();
    if (username?.trim()) {
      params = params.set('username', username.trim());
    }
    return this.http.get<User[]>(this.baseUrl, { params });
  }

  create(username: string, email: string, password: string): Observable<User> {
    return this.http.post<User>(this.baseUrl, { username, email, password });
  }
}
