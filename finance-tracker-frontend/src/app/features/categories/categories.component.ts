import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Category,
  CategoryRequest,
  CategoryType,
} from '../../core/models/category.model';
import { CategoryService } from '../../core/services/category.service';
import { AlertService } from '../../core/services/alert.service';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.scss',
})
export class CategoriesComponent implements OnInit {
  private readonly categoryService = inject(CategoryService);
  private readonly alertService = inject(AlertService);

  loading = false;
  error: string | null = null;
  categories: Category[] = [];

  showAddForm = false;
  searchName = '';

  form: CategoryRequest = { name: '', categoryType: 'EXPENSE' };
  editId: string | null = null;
  editForm: CategoryRequest = { name: '', categoryType: 'EXPENSE' };

  readonly categoryTypes: CategoryType[] = ['INCOME', 'EXPENSE'];

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.error = null;
    this.categoryService.getAll(this.searchName || undefined).subscribe({
      next: (data) => {
        this.categories = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Impossible de charger les catégories.';
        this.loading = false;
      },
    });
  }

  createCategory(): void {
    if (!this.form.name.trim()) {
      this.error = 'Le nom est obligatoire.';
      return;
    }
    this.categoryService.create(this.form).subscribe({
      next: () => {
        this.form.name = '';
        this.form.categoryType = 'EXPENSE';
        this.showAddForm = false;
        this.alertService.success('Catégorie créée.');
        this.loadCategories();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Échec de création de la catégorie.';
        this.alertService.error(this.error);
      },
    });
  }

  startEdit(category: Category): void {
    this.editId = category.id;
    this.editForm = { name: category.name, categoryType: category.categoryType };
  }

  cancelEdit(): void {
    this.editId = null;
  }

  saveEdit(): void {
    if (!this.editId || !this.editForm.name.trim()) {
      this.error = 'Le nom est obligatoire.';
      return;
    }
    this.categoryService.update(this.editId, this.editForm).subscribe({
      next: () => {
        this.editId = null;
        this.alertService.success('Catégorie modifiée.');
        this.loadCategories();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Échec de modification.';
        this.alertService.error(this.error);
      },
    });
  }

  async deleteCategory(id: string): Promise<void> {
    const confirmed = await this.alertService.confirmDelete('cette catégorie');
    if (!confirmed) return;
    this.categoryService.delete(id).subscribe({
      next: () => {
        this.alertService.success('Catégorie supprimée.');
        this.loadCategories();
      },
      error: (err) => {
        console.error(err);
        this.alertService.error('Échec de suppression.');
      },
    });
  }

  get incomeCategories(): Category[] {
    return this.categories.filter((c) => c.categoryType === 'INCOME');
  }

  get expenseCategories(): Category[] {
    return this.categories.filter((c) => c.categoryType === 'EXPENSE');
  }

  get incomeCount(): number {
    return this.incomeCategories.length;
  }

  get expenseCount(): number {
    return this.expenseCategories.length;
  }
}
