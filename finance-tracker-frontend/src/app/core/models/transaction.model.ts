import { Category } from './category.model';

export type TransactionType = 'INCOME' | 'EXPENSE';
export type Frequency = 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'PUNCTUAL';

export interface Transaction {
  id: string;
  user?: { id: string };
  category?: Category | null;
  amount: number;
  date: string;
  frequency?: Frequency | null;
  description?: string | null;
  transactionType: TransactionType;
}

export interface TransactionRequest {
  userId: string;
  categoryId?: string | null;
  amount: number;
  date: string;
  frequency?: Frequency | null;
  description?: string | null;
  transactionType: TransactionType;
}
