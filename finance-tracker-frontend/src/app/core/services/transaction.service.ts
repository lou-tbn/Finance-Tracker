import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Transaction,
  TransactionRequest,
  TransactionType,
} from '../models/transaction.model';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/transactions`;

  getAll(userId?: string, transactionType?: TransactionType): Observable<Transaction[]> {
    let params = new HttpParams();
    if (userId?.trim()) {
      params = params.set('userId', userId.trim());
    }
    if (transactionType) {
      params = params.set('transactionType', transactionType);
    }
    return this.http.get<Transaction[]>(this.baseUrl, { params });
  }

  create(payload: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.baseUrl, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
