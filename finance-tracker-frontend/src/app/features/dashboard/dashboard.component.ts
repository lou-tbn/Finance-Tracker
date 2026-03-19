import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../core/services/dashboard.service';
import { Dashboard } from '../../core/models/dashboard.model';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  private readonly authService = inject(AuthService);

  // Pour l'instant, userId en dur pour la démo; on pourra brancher sur un vrai user plus tard.
  userId = '';

  loading = false;
  error: string | null = null;
  data: Dashboard | null = null;

  ngOnInit(): void {
    const activeUserId = this.authService.getCurrentUserId();
    if (activeUserId) {
      this.userId = activeUserId;
      this.loadDashboard();
    }
  }

  loadDashboard(): void {
    if (!this.userId) {
      this.error = 'Please enter a user id (UUID).';
      return;
    }
    this.loading = true;
    this.error = null;
    this.data = null;

    this.dashboardService.getDashboard(this.userId).subscribe({
      next: (d) => {
        this.data = d;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load dashboard for this user.';
        this.loading = false;
      },
    });
  }
}

