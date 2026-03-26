import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Category } from '../../core/models/category.model';
import {
  Frequency,
  Transaction,
  TransactionFilters,
  TransactionRequest,
  TransactionType,
} from '../../core/models/transaction.model';
import { CategoryService } from '../../core/services/category.service';
import { TransactionService } from '../../core/services/transaction.service';
import { AuthService } from '../../core/services/auth.service';
import { AlertService } from '../../core/services/alert.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.scss',
})
export class TransactionsComponent implements OnInit, OnDestroy {
  private readonly transactionService = inject(TransactionService);
  private readonly categoryService = inject(CategoryService);
  private readonly authService = inject(AuthService);
  private readonly alertService = inject(AlertService);

  private sub?: Subscription;

  loading = false;
  error: string | null = null;
  transactions: Transaction[] = [];
  categories: Category[] = [];

  showAddForm = false;
  showFilters = false;

  filterUserId = '';
  filterCategoryId = '';
  filterStart = '';
  filterEnd = '';
  filterMinAmount: number | null = null;
  filterMaxAmount: number | null = null;
  filterSearch = '';
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

  editId: string | null = null;
  editForm: {
    categoryId: string;
    amount: number | null;
    date: string;
    frequency: '' | Frequency;
    description: string;
    transactionType: TransactionType;
  } = {
    categoryId: '',
    amount: null,
    date: '',
    frequency: '',
    description: '',
    transactionType: 'EXPENSE',
  };

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId() ?? '';
    this.filterUserId = userId;
    this.form.userId = userId;
    this.loadCategories();
    this.loadTransactions();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => (this.categories = data),
      error: (err) => console.error(err),
    });
  }

  loadTransactions(): void {
    const userId = (this.filterUserId || this.authService.getCurrentUserId() || '').trim();
    if (!userId) {
      this.transactions = [];
      this.error = 'Aucun utilisateur actif. Reconnecte-toi.';
      this.loading = false;
      return;
    }
    this.filterUserId = userId;
    this.form.userId = userId;
    this.loading = true;
    this.error = null;

    const filters: TransactionFilters = {
      userId,
      categoryId: this.filterCategoryId || undefined,
      start: this.filterStart || undefined,
      end: this.filterEnd || undefined,
      minAmount: this.filterMinAmount,
      maxAmount: this.filterMaxAmount,
      transactionType: this.filterType || undefined,
      search: this.filterSearch || undefined,
    };

    this.sub?.unsubscribe();
    this.sub = this.transactionService.getAll(filters).subscribe({
      next: (data) => {
        this.transactions = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error =
          typeof err?.error === 'string'
            ? `Transactions indisponibles : ${err.error}`
            : 'Impossible de charger les transactions.';
        this.loading = false;
      },
    });
  }

  createTransaction(): void {
    const userId = (this.form.userId || this.authService.getCurrentUserId() || '').trim();
    if (!userId || !this.form.amount || !this.form.date) {
      this.error = 'Montant, date et utilisateur connecté sont obligatoires.';
      return;
    }
    const payload: TransactionRequest = {
      userId,
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
        this.form.categoryId = '';
        this.form.frequency = '';
        this.form.transactionType = 'EXPENSE';
        this.showAddForm = false;
        this.alertService.success('Transaction ajoutée.');
        this.loadTransactions();
      },
      error: (err) => {
        console.error(err);
        this.error =
          typeof err?.error === 'string' ? `Échec : ${err.error}` : 'Échec de création.';
        this.alertService.error(this.error ?? '');
      },
    });
  }

  startEdit(transaction: Transaction): void {
    this.editId = transaction.id;
    this.editForm = {
      categoryId: transaction.category?.id || '',
      amount: Number(transaction.amount),
      date: transaction.date ? this.toInputDate(transaction.date) : '',
      frequency: transaction.frequency || '',
      description: transaction.description || '',
      transactionType: transaction.transactionType,
    };
  }

  cancelEdit(): void {
    this.editId = null;
  }

  saveEdit(): void {
    if (!this.editId) return;
    const userId = (this.form.userId || this.authService.getCurrentUserId() || '').trim();
    if (!userId || !this.editForm.amount || !this.editForm.date) {
      this.error = 'Montant, date et utilisateur connecté sont obligatoires.';
      return;
    }
    const payload: TransactionRequest = {
      userId,
      categoryId: this.editForm.categoryId || null,
      amount: this.editForm.amount,
      date: this.editForm.date,
      frequency: this.editForm.frequency || null,
      description: this.editForm.description || null,
      transactionType: this.editForm.transactionType,
    };
    this.transactionService.update(this.editId, payload).subscribe({
      next: () => {
        this.editId = null;
        this.alertService.success('Transaction modifiée.');
        this.loadTransactions();
      },
      error: (err) => {
        console.error(err);
        this.error =
          typeof err?.error === 'string' ? `Échec : ${err.error}` : 'Échec de modification.';
        this.alertService.error(this.error ?? '');
      },
    });
  }

  async deleteTransaction(id: string): Promise<void> {
    const confirmed = await this.alertService.confirmDelete('cette transaction');
    if (!confirmed) return;
    this.transactionService.delete(id).subscribe({
      next: () => {
        this.alertService.success('Transaction supprimée.');
        this.loadTransactions();
      },
      error: () => this.alertService.error('Échec de suppression.'),
    });
  }

  generateRecurring(): void {
    const userId = this.authService.getCurrentUserId() ?? '';
    if (!userId) return;
    this.transactionService.generateRecurring(userId).subscribe({
      next: (generated) => {
        if (generated.length === 0) {
          this.alertService.success('Aucune nouvelle transaction à générer (déjà fait ce mois-ci).');
        } else {
          this.alertService.success(`${generated.length} transaction(s) récurrente(s) générée(s).`);
        }
        this.loadTransactions();
      },
      error: () => this.alertService.error('Échec de la génération des transactions récurrentes.'),
    });
  }

  resetFilters(): void {
    this.filterCategoryId = '';
    this.filterType = '';
    this.filterStart = '';
    this.filterEnd = '';
    this.filterMinAmount = null;
    this.filterMaxAmount = null;
    this.filterSearch = '';
    this.loadTransactions();
  }

  private toInputDate(value: string): string {
    const date = new Date(value);
    const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 16);
  }

  get filteredCategoriesForCreate(): typeof this.categories {
    return this.categories.filter(c => c.categoryType === this.form.transactionType);
  }

  get filteredCategoriesForEdit(): typeof this.categories {
    return this.categories.filter(c => c.categoryType === this.editForm.transactionType);
  }

  get totalIncome(): number {
    return this.transactions
      .filter((t) => t.transactionType === 'INCOME')
      .reduce((acc, t) => acc + Number(t.amount), 0);
  }

  get totalExpense(): number {
    return this.transactions
      .filter((t) => t.transactionType === 'EXPENSE')
      .reduce((acc, t) => acc + Number(t.amount), 0);
  }
}
