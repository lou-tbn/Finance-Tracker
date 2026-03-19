import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Category } from '../../core/models/category.model';
import {
  Frequency,
  Transaction,
  TransactionRequest,
  TransactionType,
} from '../../core/models/transaction.model';
import { CategoryService } from '../../core/services/category.service';
import { TransactionService } from '../../core/services/transaction.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.scss',
})
export class TransactionsComponent implements OnInit {
  private readonly transactionService = inject(TransactionService);
  private readonly categoryService = inject(CategoryService);
  private readonly authService = inject(AuthService);

  loading = false;
  error: string | null = null;
  transactions: Transaction[] = [];
  categories: Category[] = [];

  filterUserId = '';
  filterType: '' | TransactionType = '';

  readonly transactionTypes: TransactionType[] = ['INCOME', 'EXPENSE'];
  readonly frequencies: Frequency[] = ['DAILY', 'WEEKLY', 'MONTHLY', 'PUNCTUAL'];

  form: {
    userId: string;
    categoryId: string;
    amount: number | null;
    date: string;
    frequency: '' | Frequency;
    description: string;
    transactionType: TransactionType;
  } = {
    userId: '',
    categoryId: '',
    amount: null,
    date: '',
    frequency: '',
    description: '',
    transactionType: 'EXPENSE',
  };

  ngOnInit(): void {
    const activeUserId = this.authService.getCurrentUserId();
    if (activeUserId) {
      this.filterUserId = activeUserId;
      this.form.userId = activeUserId;
    }
    this.loadCategories();
    this.loadTransactions();
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => (this.categories = data),
      error: (err) => console.error(err),
    });
  }

  loadTransactions(): void {
    this.loading = true;
    this.error = null;
    this.transactionService
      .getAll(this.filterUserId || undefined, this.filterType || undefined)
      .subscribe({
        next: (data) => {
          this.transactions = data;
          this.loading = false;
        },
        error: (err) => {
          console.error(err);
          this.error = 'Impossible de charger les transactions.';
          this.loading = false;
        },
      });
  }

  createTransaction(): void {
    if (!this.form.userId || !this.form.amount || !this.form.date) {
      this.error = 'UserId, amount et date sont obligatoires.';
      return;
    }

    const payload: TransactionRequest = {
      userId: this.form.userId.trim(),
      categoryId: this.form.categoryId || null,
      amount: this.form.amount,
      date: this.form.date,
      frequency: this.form.frequency || null,
      description: this.form.description || null,
      transactionType: this.form.transactionType,
    };

    this.transactionService.create(payload).subscribe({
      next: () => {
        this.form.amount = null;
        this.form.date = '';
        this.form.description = '';
        this.loadTransactions();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Echec de creation de la transaction.';
      },
    });
  }

  deleteTransaction(id: string): void {
    if (!confirm('Supprimer cette transaction ?')) {
      return;
    }
    this.transactionService.delete(id).subscribe({
      next: () => this.loadTransactions(),
      error: (err) => {
        console.error(err);
        this.error = 'Echec de suppression.';
      },
    });
  }
}
