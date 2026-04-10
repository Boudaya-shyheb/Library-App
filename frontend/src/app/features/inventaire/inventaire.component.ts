import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventaireService } from './inventaire.service';
import { Book } from './inventaire.models';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-inventaire',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventaire.component.html',
  styleUrl: './inventaire.component.css'
})
export class InventaireComponent implements OnInit {
  books: Book[] = [];
  loading = false;
  form: Book = this.emptyForm();
  editingId: number | null = null;

  constructor(private service: InventaireService, private toast: ToastService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.service.list().subscribe({
      next: res => {
        this.books = res.data.content || [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toast.show('error', 'Erreur lors du chargement des livres');
      }
    });
  }

  submit(): void {
    if (this.editingId) {
      this.service.update(this.editingId, this.form).subscribe({
        next: () => {
          this.toast.show('success', 'Livre modifie');
          this.cancelEdit();
          this.load();
        },
        error: () => this.toast.show('error', 'Echec de modification')
      });
      return;
    }
    this.service.create(this.form).subscribe({
      next: () => {
        this.toast.show('success', 'Livre ajoute');
        this.form = this.emptyForm();
        this.load();
      },
      error: () => this.toast.show('error', 'Echec de creation')
    });
  }

  edit(item: Book): void {
    this.editingId = item.bookId ?? null;
    this.form = { ...item };
  }

  delete(id?: number): void {
    if (!id || !confirm('Supprimer ce livre ?')) {
      return;
    }
    this.service.remove(id).subscribe({
      next: () => {
        this.toast.show('success', 'Livre supprime');
        this.load();
      },
      error: () => this.toast.show('error', 'Echec de suppression')
    });
  }

  cancelEdit(): void {
    this.editingId = null;
    this.form = this.emptyForm();
  }

  private emptyForm(): Book {
    return { title: '', author: '', isbn: '', category: '', publicationYear: new Date().getFullYear(), quantity: 0 };
  }
}
