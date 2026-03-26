import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Goal, GoalRequest } from '../../core/models/goal.model';
import { GoalService } from '../../core/services/goal.service';
import { AuthService } from '../../core/services/auth.service';
import { AlertService } from '../../core/services/alert.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-goals',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './goals.component.html',
  styleUrl: './goals.component.scss',
})
export class GoalsComponent implements OnInit, OnDestroy {
  private readonly goalService = inject(GoalService);
  private readonly authService = inject(AuthService);
  private readonly alertService = inject(AlertService);

  private sub?: Subscription;

  loading = false;
  error: string | null = null;
  goals: Goal[] = [];

  showAddForm = false;
  filterUserId = '';
  filterTitle = '';

  /** Couleurs assignées aux cartes dans l'ordre */
  readonly cardColors = [
    '#6366f1', '#8b5cf6', '#ec4899', '#f97316',
    '#f59e0b', '#10b981', '#06b6d4', '#0ea5e9',
  ];

  form: {
    userId: string;
    title: string;
    targetAmount: number | null;
    currentAmount: number | null;
    deadline: string;
  } = { userId: '', title: '', targetAmount: null, currentAmount: 0, deadline: '' };

  transferGoalId: string | null = null;
  transferGoalTitle = '';
  transferAmount: number | null = null;

  withdrawGoalId: string | null = null;
  withdrawGoalTitle = '';
  withdrawGoalMax = 0;
  withdrawAmount: number | null = null;

  editId: string | null = null;
  editForm: {
    title: string;
    targetAmount: number | null;
    currentAmount: number | null;
    deadline: string;
  } = { title: '', targetAmount: null, currentAmount: 0, deadline: '' };

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId() ?? '';
    this.filterUserId = userId;
    this.form.userId = userId;
    this.loadGoals();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  loadGoals(): void {
    const effectiveUserId = (this.filterUserId || this.authService.getCurrentUserId() || '').trim();
    this.filterUserId = effectiveUserId;
    this.form.userId = effectiveUserId;

    this.loading = true;
    this.error = null;
    this.sub?.unsubscribe();
    this.sub = this.goalService
      .getAll(effectiveUserId || undefined, this.filterTitle || undefined)
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
    const effectiveUserId = (this.form.userId || this.authService.getCurrentUserId() || '').trim();
    if (!effectiveUserId || !this.form.title || !this.form.targetAmount) {
      this.error = 'Titre, objectif et utilisateur connecté sont obligatoires.';
      return;
    }
    const payload: GoalRequest = {
      userId: effectiveUserId,
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
        this.showAddForm = false;
        this.alertService.success('Objectif créé.');
        this.loadGoals();
      },
      error: (err) => {
        console.error(err);
        this.error = "Échec de création de l'objectif.";
        this.alertService.error(this.error);
      },
    });
  }

  startEdit(goal: Goal): void {
    this.editId = goal.id;
    this.editForm = {
      title: goal.title,
      targetAmount: goal.targetAmount,
      currentAmount: goal.currentAmount,
      deadline: goal.deadline ? this.toInputDate(goal.deadline) : '',
    };
  }

  cancelEdit(): void {
    this.editId = null;
  }

  saveEdit(): void {
    if (!this.editId) return;
    const effectiveUserId = (this.form.userId || this.authService.getCurrentUserId() || '').trim();
    if (!effectiveUserId || !this.editForm.title.trim() || !this.editForm.targetAmount) {
      this.error = 'Titre et montant cible sont obligatoires.';
      return;
    }
    const payload: GoalRequest = {
      userId: effectiveUserId,
      title: this.editForm.title.trim(),
      targetAmount: this.editForm.targetAmount,
      currentAmount: this.editForm.currentAmount ?? 0,
      deadline: this.editForm.deadline || null,
    };
    this.goalService.update(this.editId, payload).subscribe({
      next: () => {
        this.editId = null;
        this.alertService.success('Objectif modifié.');
        this.loadGoals();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Échec de modification.';
        this.alertService.error(this.error);
      },
    });
  }

  async deleteGoal(id: string): Promise<void> {
    const confirmed = await this.alertService.confirmDelete('cet objectif');
    if (!confirmed) return;
    this.goalService.delete(id).subscribe({
      next: () => {
        this.alertService.success('Objectif supprimé.');
        this.loadGoals();
      },
      error: (err) => {
        console.error(err);
        this.alertService.error('Échec de suppression.');
      },
    });
  }

  openTransfer(goal: Goal): void {
    this.transferGoalId = goal.id;
    this.transferGoalTitle = goal.title;
    this.transferAmount = null;
  }

  cancelTransfer(): void {
    this.transferGoalId = null;
    this.transferAmount = null;
  }

  confirmTransfer(): void {
    if (!this.transferGoalId || !this.transferAmount || this.transferAmount <= 0) return;
    const userId = (this.form.userId || this.authService.getCurrentUserId() || '').trim();
    this.goalService.transfer(this.transferGoalId, userId, this.transferAmount).subscribe({
      next: () => {
        this.alertService.success(`${this.transferAmount} € virés vers "${this.transferGoalTitle}".`);
        this.cancelTransfer();
        this.loadGoals();
      },
      error: (err) => {
        const msg = typeof err?.error === 'string' ? err.error : 'Virement échoué.';
        this.alertService.error(msg);
      },
    });
  }

  openWithdraw(goal: Goal): void {
    this.withdrawGoalId = goal.id;
    this.withdrawGoalTitle = goal.title;
    this.withdrawGoalMax = goal.currentAmount;
    this.withdrawAmount = null;
  }

  cancelWithdraw(): void {
    this.withdrawGoalId = null;
    this.withdrawAmount = null;
  }

  confirmWithdraw(): void {
    if (!this.withdrawGoalId || !this.withdrawAmount || this.withdrawAmount <= 0) return;
    const userId = (this.form.userId || this.authService.getCurrentUserId() || '').trim();
    this.goalService.withdraw(this.withdrawGoalId, userId, this.withdrawAmount).subscribe({
      next: () => {
        this.alertService.success(`${this.withdrawAmount} € retirés depuis "${this.withdrawGoalTitle}" vers ton solde.`);
        this.cancelWithdraw();
        this.loadGoals();
      },
      error: (err) => {
        const msg = typeof err?.error === 'string' ? err.error : 'Retrait échoué.';
        this.alertService.error(msg);
      },
    });
  }

  getProgress(goal: Goal): number {
    if (!goal.targetAmount) return 0;
    return Math.min(100, Math.round((goal.currentAmount / goal.targetAmount) * 100));
  }

  getColor(index: number): string {
    return this.cardColors[index % this.cardColors.length];
  }

  /** strokeDashoffset pour un cercle SVG r=28 (circumférence ≈ 175.9) */
  getStrokeDashoffset(goal: Goal): number {
    const circumference = 2 * Math.PI * 28;
    return circumference - (circumference * this.getProgress(goal)) / 100;
  }

  get circumference(): number {
    return 2 * Math.PI * 28;
  }

  get completedGoals(): number {
    return this.goals.filter((g) => this.getProgress(g) >= 100).length;
  }

  get totalTarget(): number {
    return this.goals.reduce((acc, g) => acc + Number(g.targetAmount), 0);
  }

  private toInputDate(value: string): string {
    const date = new Date(value);
    const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 16);
  }
}
