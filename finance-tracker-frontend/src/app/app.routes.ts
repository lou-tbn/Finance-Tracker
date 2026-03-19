import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(
        (m) => m.DashboardComponent,
      ),
  },
  {
    path: 'transactions',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/transactions/transactions.component').then(
        (m) => m.TransactionsComponent,
      ),
  },
  {
    path: 'categories',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/categories/categories.component').then(
        (m) => m.CategoriesComponent,
      ),
  },
  {
    path: 'goals',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/goals/goals.component').then((m) => m.GoalsComponent),
  },
  {
    path: '**',
    redirectTo: '',
  },
];
