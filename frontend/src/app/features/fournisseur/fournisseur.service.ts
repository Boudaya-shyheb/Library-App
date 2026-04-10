import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { ApiEnvelope, Fournisseur } from './fournisseur.models';

@Injectable({ providedIn: 'root' })
export class FournisseurService {
  constructor(private api: ApiService) {}

  list(): Observable<ApiEnvelope<{ content: Fournisseur[] }>> {
    return this.api.get<ApiEnvelope<{ content: Fournisseur[] }>>('/api/fournisseurs?page=0&size=50');
  }

  create(payload: Fournisseur): Observable<ApiEnvelope<Fournisseur>> {
    return this.api.post<ApiEnvelope<Fournisseur>>('/api/fournisseurs', payload);
  }

  update(id: number, payload: Fournisseur): Observable<ApiEnvelope<Fournisseur>> {
    return this.api.put<ApiEnvelope<Fournisseur>>(`/api/fournisseurs/${id}`, payload);
  }

  remove(id: number): Observable<ApiEnvelope<void>> {
    return this.api.delete<ApiEnvelope<void>>(`/api/fournisseurs/${id}`);
  }
}
