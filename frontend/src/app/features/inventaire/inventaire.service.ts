import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { ApiEnvelope, Book } from './inventaire.models';

@Injectable({ providedIn: 'root' })
export class InventaireService {
  constructor(private api: ApiService) {}

  list(): Observable<ApiEnvelope<{ content: Book[] }>> {
    return this.api.get<ApiEnvelope<{ content: Book[] }>>('/api/inventaire/books?page=0&size=50');
  }

  create(payload: Book): Observable<ApiEnvelope<Book>> {
    return this.api.post<ApiEnvelope<Book>>('/api/inventaire/books', payload);
  }

  update(id: number, payload: Book): Observable<ApiEnvelope<Book>> {
    return this.api.put<ApiEnvelope<Book>>(`/api/inventaire/books/${id}`, payload);
  }

  remove(id: number): Observable<ApiEnvelope<void>> {
    return this.api.delete<ApiEnvelope<void>>(`/api/inventaire/books/${id}`);
  }
}
