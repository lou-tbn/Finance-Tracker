import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Category,
  CategoryRequest,
  CategoryType,
} from '../../core/models/category.model';
import { CategoryService } from '../../core/services/category.service';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.scss',
})
export class CategoriesComponent implements OnInit {
  private readonly categoryService = inject(CategoryService);

  loading = false;
  error: string | null = null;
  categories: Category[] = [];

  searchName = '';
  form: CategoryRequest = {
    name: '',
    categoryType: 'EXPENSE',
  };

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
        this.error = 'Impossible de charger les categories.';
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
        this.loadCategories();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Echec de creation de la categorie.';
      },
    });
  }

  deleteCategory(id: string): void {
    if (!confirm('Supprimer cette categorie ?')) {
      return;
    }
    this.categoryService.delete(id).subscribe({
      next: () => this.loadCategories(),
      error: (err) => {
        console.error(err);
        this.error = 'Echec de suppression.';
      },
    });
  }
}
