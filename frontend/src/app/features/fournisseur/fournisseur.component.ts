import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FournisseurService } from './fournisseur.service';
import { Fournisseur } from './fournisseur.models';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-fournisseur',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './fournisseur.component.html',
  styleUrl: './fournisseur.component.css'
})
export class FournisseurComponent implements OnInit {
  fournisseurs: Fournisseur[] = [];
  loading = false;
  form: Fournisseur = this.emptyForm();
  editingId: number | null = null;

  constructor(private service: FournisseurService, private toast: ToastService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.service.list().subscribe({
      next: res => {
        this.fournisseurs = res.data.content || [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toast.show('error', 'Erreur lors du chargement des fournisseurs');
      }
    });
  }

  submit(): void {
    if (this.editingId) {
      this.service.update(this.editingId, this.form).subscribe({
        next: () => {
          this.toast.show('success', 'Fournisseur modifie');
          this.cancelEdit();
          this.load();
        },
        error: () => this.toast.show('error', 'Echec de modification')
      });
      return;
    }
    this.service.create(this.form).subscribe({
      next: () => {
        this.toast.show('success', 'Fournisseur ajoute');
        this.form = this.emptyForm();
        this.load();
      },
      error: () => this.toast.show('error', 'Echec de creation')
    });
  }

  edit(item: Fournisseur): void {
    this.editingId = item.id ?? null;
    this.form = { ...item };
  }

  delete(id?: number): void {
    if (!id || !confirm('Supprimer ce fournisseur ?')) {
      return;
    }
    this.service.remove(id).subscribe({
      next: () => {
        this.toast.show('success', 'Fournisseur supprime');
        this.load();
      },
      error: () => this.toast.show('error', 'Echec de suppression')
    });
  }

  cancelEdit(): void {
    this.editingId = null;
    this.form = this.emptyForm();
  }

  private emptyForm(): Fournisseur {
    return { nom: '', email: '', telephone: '', adresse: '' };
  }
}
