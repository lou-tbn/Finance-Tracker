import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
  private auth = inject(AuthService);

  get user() {
    return this.auth.getCurrentUser();
  }

  get initials(): string {
    const name = this.user?.username ?? '?';
    return name.charAt(0).toUpperCase();
  }

  logout(): void {
    this.auth.logout();
  }
}
