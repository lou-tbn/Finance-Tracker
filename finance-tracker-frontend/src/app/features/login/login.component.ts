import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  password = '';
  loading = false;
  error: string | null = null;

  onSubmit(): void {
    if (!this.email.trim() || !this.password) {
      this.error = 'Email et mot de passe sont obligatoires.';
      return;
    }

    this.loading = true;
    this.error = null;

    this.authService.login(this.email, this.password).subscribe({
      next: (user) => {
        this.loading = false;
        if (!user) {
          this.error = 'Identifiants invalides.';
          return;
        }
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.error = 'Impossible de se connecter.';
      },
    });
  }
}
