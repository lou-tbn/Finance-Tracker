import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Category, CategoryRequest } from '../models/category.model';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/categories`;

  getAll(name?: string): Observable<Category[]> {
    if (name && name.trim()) {
      return this.http.get<Category[]>(`${this.baseUrl}?name=${encodeURIComponent(name)}`);
    }
    return this.http.get<Category[]>(this.baseUrl);
  }

  create(payload: CategoryRequest): Observable<Category> {
    return this.http.post<Category>(this.baseUrl, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
