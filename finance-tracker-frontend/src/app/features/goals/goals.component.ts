import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Goal, GoalRequest } from '../../core/models/goal.model';
import { GoalService } from '../../core/services/goal.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-goals',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './goals.component.html',
  styleUrl: './goals.component.scss',
})
export class GoalsComponent implements OnInit {
  private readonly goalService = inject(GoalService);
  private readonly authService = inject(AuthService);

  loading = false;
  error: string | null = null;
  goals: Goal[] = [];

  filterUserId = '';
  filterTitle = '';

  form: {
    userId: string;
    title: string;
    targetAmount: number | null;
    currentAmount: number | null;
    deadline: string;
  } = {
    userId: '',
    title: '',
    targetAmount: null,
    currentAmount: 0,
    deadline: '',
  };

  ngOnInit(): void {
    const activeUserId = this.authService.getCurrentUserId();
    if (activeUserId) {
      this.filterUserId = activeUserId;
      this.form.userId = activeUserId;
    }
    this.loadGoals();
  }

  loadGoals(): void {
    this.loading = true;
    this.error = null;
    this.goalService
      .getAll(this.filterUserId || undefined, this.filterTitle || undefined)
      .subscribe({
        next: (data) => {
          this.goals = data;
          this.loading = false;
        },
        error: (err) => {
          console.error(err);
          this.error = 'Impossible de charger les objectifs.';
          this.loading = false;
        },
      });
  }

  createGoal(): void {
    if (!this.form.userId || !this.form.title || !this.form.targetAmount) {
      this.error = 'userId, titre et targetAmount sont obligatoires.';
      return;
    }

    const payload: GoalRequest = {
      userId: this.form.userId.trim(),
      title: this.form.title.trim(),
      targetAmount: this.form.targetAmount,
      currentAmount: this.form.currentAmount ?? 0,
      deadline: this.form.deadline || null,
    };

    this.goalService.create(payload).subscribe({
      next: () => {
        this.form.title = '';
        this.form.targetAmount = null;
        this.form.currentAmount = 0;
        this.form.deadline = '';
        this.loadGoals();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Echec de creation de l objectif.';
      },
    });
  }

  deleteGoal(id: string): void {
    if (!confirm('Supprimer cet objectif ?')) {
      return;
    }
    this.goalService.delete(id).subscribe({
      next: () => this.loadGoals(),
      error: (err) => {
        console.error(err);
        this.error = 'Echec de suppression.';
      },
    });
  }

  getProgress(goal: Goal): number {
    if (!goal.targetAmount) {
      return 0;
    }
    return Math.min(100, (goal.currentAmount / goal.targetAmount) * 100);
  }
}
