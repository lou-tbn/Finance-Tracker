import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  username = '';
  email = '';
  password = '';
  confirmPassword = '';
  loading = false;
  error: string | null = null;

  onSubmit(): void {
    this.error = null;

    if (!this.username.trim() || !this.email.trim() || !this.password) {
      this.error = 'Tous les champs sont obligatoires.';
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.error = 'Les mots de passe ne correspondent pas.';
      return;
    }
    if (this.password.length < 6) {
      this.error = 'Le mot de passe doit contenir au moins 6 caractères.';
      return;
    }

    this.loading = true;
    this.userService.create(this.username.trim(), this.email.trim(), this.password).subscribe({
      next: () => {
        this.router.navigateByUrl('/login');
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.error = typeof err?.error === 'string' ? err.error : 'Échec de la création du compte.';
      },
    });
  }
}
