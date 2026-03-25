import { Category } from './category.model';

export type TransactionType = 'INCOME' | 'EXPENSE';
export type Frequency = 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'PUNCTUAL';

export interface Transaction {
  id: string;
  user?: { id: string };
  category?: Category | null;
  isTemplate?: boolean;
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

export interface TransactionFilters {
  userId: string;
  categoryId?: string;
  start?: string;
  end?: string;
  minAmount?: number | null;
  maxAmount?: number | null;
  transactionType?: TransactionType;
  search?: string;
}
