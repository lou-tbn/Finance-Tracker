import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dashboard } from '../../core/models/dashboard.model';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { retry, Subscription, timeout } from 'rxjs';
import { DashboardService } from '../../core/services/dashboard.service';
import { AlertService } from '../../core/services/alert.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly dashboardService = inject(DashboardService);
  private readonly alertService = inject(AlertService);

  private sub?: Subscription;

  userId = '';
  selectedMonth = this.getCurrentMonthValue();

  loading = false;
  error: string | null = null;
  data: Dashboard | null = null;

  readonly categoryColors = [
    '#6366f1', '#8b5cf6', '#ec4899', '#f97316',
    '#f59e0b', '#10b981', '#06b6d4', '#0ea5e9',
  ];

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUserId() ?? '';
    this.loadDashboard();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  loadDashboard(): void {
    const userId = (this.userId || this.authService.getCurrentUserId() || '').trim();
    if (!userId) {
      this.error = 'Aucun utilisateur connecté.';
      return;
    }
    this.userId = userId;

    this.sub?.unsubscribe();
    this.loading = true;
    this.error = null;
    this.data = null;

    const [year, month] = this.selectedMonth.split('-').map(Number);

    this.sub = this.dashboardService
      .getDashboard(userId, month, year)
      .pipe(timeout(10000), retry({ count: 1, delay: 300 }))
      .subscribe({
        next: (dashboard) => {
          this.data = dashboard;
          this.loading = false;
        },
        error: (err) => {
          console.error(err);
          this.loading = false;
          this.error = this.formatLoadError(err);
          this.alertService.error(this.error);
        },
      });
  }

  onMonthChange(): void {
    this.loadDashboard();
  }

  getCategoryColor(index: number): string {
    return this.categoryColors[index % this.categoryColors.length];
  }

  getCategoryPercent(total: number): number {
    if (!this.data?.expensesByCategory.length) return 0;
    const max = Math.max(...this.data.expensesByCategory.map((e) => Number(e.total)));
    return max > 0 ? (Number(total) / max) * 100 : 0;
  }

  get savingsRate(): number {
    if (!this.data || !this.data.totalIncomeThisMonth) return 0;
    const rate = (this.data.balance / this.data.totalIncomeThisMonth) * 100;
    return Math.max(0, Math.min(100, rate));
  }

  private getCurrentMonthValue(): string {
    const now = new Date();
    const month = `${now.getMonth() + 1}`.padStart(2, '0');
    return `${now.getFullYear()}-${month}`;
  }

  private formatLoadError(err: unknown): string {
    const typed = err as { name?: string; error?: unknown };
    if (typed?.name === 'TimeoutError') {
      return 'Le chargement prend trop de temps. Vérifie que le backend est disponible.';
    }
    if (typeof typed?.error === 'string') return typed.error;
    return 'Impossible de charger les données du dashboard.';
  }
}
