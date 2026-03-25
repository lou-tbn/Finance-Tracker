import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class AlertService {
  success(message: string): void {
    void Swal.fire({
      icon: 'success',
      title: 'Succes',
      text: message,
      timer: 1700,
      showConfirmButton: false,
    });
  }

  error(message: string): void {
    void Swal.fire({
      icon: 'error',
      title: 'Erreur',
      text: message,
    });
  }

  async confirmDelete(label: string): Promise<boolean> {
    const result = await Swal.fire({
      icon: 'warning',
      title: 'Confirmer la suppression',
      text: `Supprimer ${label} ?`,
      showCancelButton: true,
      confirmButtonText: 'Oui, supprimer',
      cancelButtonText: 'Annuler',
      confirmButtonColor: '#dc3545',
    });
    return result.isConfirmed;
  }
}
