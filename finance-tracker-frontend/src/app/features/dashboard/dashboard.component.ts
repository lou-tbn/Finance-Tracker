import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dashboard } from '../../core/models/dashboard.model';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { firstValueFrom, retry, timeout } from 'rxjs';
import { DashboardService } from '../../core/services/dashboard.service';
import { AlertService } from '../../core/services/alert.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly dashboardService = inject(DashboardService);
  private readonly alertService = inject(AlertService);
  private readonly destroyRef = inject(DestroyRef);

  private requestSeq = 0;

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
    this.authService.currentUser$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user) => {
        this.userId = user?.id ?? '';
        if (this.userId) {
          void this.loadDashboard();
        }
      });
  }

  async loadDashboard(): Promise<void> {
    const effectiveUserId = (this.userId || this.authService.getCurrentUserId() || '').trim();
    if (!effectiveUserId) {
      this.error = 'Aucun utilisateur connecté.';
      this.loading = false;
      return;
    }
    this.userId = effectiveUserId;

    const reqId = ++this.requestSeq;
    this.loading = true;
    this.error = null;

    const [year, month] = this.selectedMonth.split('-').map(Number);
    const safeMonth = Number.isFinite(month) ? month : undefined;
    const safeYear = Number.isFinite(year) ? year : undefined;

    try {
      const dashboard = await firstValueFrom(
        this.dashboardService
          .getDashboard(effectiveUserId, safeMonth, safeYear)
          .pipe(timeout(10000), retry({ count: 1, delay: 300 })),
      );
      if (this.requestSeq !== reqId) {
        return;
      }
      this.data = dashboard;
    } catch (err) {
      if (this.requestSeq !== reqId) {
        return;
      }
      console.error(err);
      this.error = this.formatLoadError(err);
      this.alertService.error(this.error);
    } finally {
      if (this.requestSeq === reqId) {
        this.loading = false;
      }
    }
  }

  onMonthChange(): void {
    void this.loadDashboard();
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
    if (typeof typed?.error === 'string') {
      return typed.error;
    }
    return 'Impossible de charger les données du dashboard.';
  }
}
