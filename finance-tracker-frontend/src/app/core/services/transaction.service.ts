import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Transaction,
  TransactionFilters,
  TransactionRequest,
} from '../models/transaction.model';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/transactions`;

  getAll(filters: TransactionFilters): Observable<Transaction[]> {
    let params = new HttpParams();
    params = params.set('userId', filters.userId.trim());

    if (filters.categoryId?.trim()) {
      params = params.set('categoryId', filters.categoryId.trim());
    }
    if (filters.start?.trim()) {
      params = params.set('start', filters.start.trim());
    }
    if (filters.end?.trim()) {
      params = params.set('end', filters.end.trim());
    }
    if (filters.minAmount != null) {
      params = params.set('minAmount', `${filters.minAmount}`);
    }
    if (filters.maxAmount != null) {
      params = params.set('maxAmount', `${filters.maxAmount}`);
    }
    if (filters.transactionType) {
      params = params.set('transactionType', filters.transactionType);
    }
    if (filters.search?.trim()) {
      params = params.set('search', filters.search.trim());
    }

    return this.http.get<Transaction[]>(this.baseUrl, { params });
  }

  create(payload: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.baseUrl, this.normalizeDate(payload));
  }

  update(id: string, payload: TransactionRequest): Observable<Transaction> {
    return this.http.put<Transaction>(`${this.baseUrl}/${id}`, this.normalizeDate(payload));
  }

  private normalizeDate(payload: TransactionRequest): TransactionRequest {
    const date = payload.date?.length === 16 ? payload.date + ':00' : payload.date;
    return { ...payload, date };
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  generateRecurring(userId: string): Observable<Transaction[]> {
    return this.http.post<Transaction[]>(`${this.baseUrl}/generate-recurring/${userId}`, {});
  }
}
