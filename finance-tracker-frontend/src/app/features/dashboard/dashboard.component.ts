import { Component, inject, OnDestroy, OnInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dashboard } from '../../core/models/dashboard.model';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { retry, Subscription, timeout } from 'rxjs';
import { DashboardService } from '../../core/services/dashboard.service';
import { AlertService } from '../../core/services/alert.service';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

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

  @ViewChild('donutCanvas') donutCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('barCanvas') barCanvas?: ElementRef<HTMLCanvasElement>;

  private donutChart?: Chart;
  private barChart?: Chart;

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
    this.donutChart?.destroy();
    this.barChart?.destroy();
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
          // Attendre que Angular rende le template avant de dessiner les charts
          setTimeout(() => this.renderCharts(), 50);
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

  private renderCharts(): void {
    if (!this.data) return;
    this.renderDonut();
    this.renderBar();
  }

  private renderDonut(): void {
    const canvas = this.donutCanvas?.nativeElement;
    if (!canvas) return;

    this.donutChart?.destroy();

    const cats = this.data!.expensesByCategory;
    if (!cats.length) return;

    this.donutChart = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: cats.map(c => c.categoryName),
        datasets: [{
          data: cats.map(c => Number(c.total)),
          backgroundColor: this.categoryColors.slice(0, cats.length),
          borderWidth: 2,
          borderColor: '#fff',
          hoverOffset: 6,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '65%',
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              padding: 16,
              font: { size: 12 },
              usePointStyle: true,
              pointStyleWidth: 8,
            },
          },
          tooltip: {
            callbacks: {
              label: ctx => ` ${ctx.label} : ${Number(ctx.raw).toFixed(2)} €`,
            },
          },
        },
      },
    });
  }

  private renderBar(): void {
    const canvas = this.barCanvas?.nativeElement;
    if (!canvas) return;

    this.barChart?.destroy();

    this.barChart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: ['Ce mois'],
        datasets: [
          {
            label: 'Revenus',
            data: [Number(this.data!.totalIncomeThisMonth)],
            backgroundColor: 'rgba(16, 185, 129, 0.8)',
            borderColor: '#10b981',
            borderWidth: 1,
            borderRadius: 6,
          },
          {
            label: 'Dépenses',
            data: [Number(this.data!.totalExpenseThisMonth)],
            backgroundColor: 'rgba(239, 68, 68, 0.8)',
            borderColor: '#ef4444',
            borderWidth: 1,
            borderRadius: 6,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              padding: 16,
              font: { size: 12 },
              usePointStyle: true,
              pointStyleWidth: 8,
            },
          },
          tooltip: {
            callbacks: {
              label: ctx => ` ${ctx.dataset.label} : ${Number(ctx.raw).toFixed(2)} €`,
            },
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: val => `${val} €`,
              font: { size: 11 },
            },
            grid: { color: 'rgba(0,0,0,0.05)' },
          },
          x: {
            grid: { display: false },
          },
        },
      },
    });
  }

  getCategoryColor(index: number): string {
    return this.categoryColors[index % this.categoryColors.length];
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
