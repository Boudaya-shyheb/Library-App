export interface Fournisseur {
  id?: number;
  nom: string;
  email: string;
  telephone?: string;
  adresse?: string;
}

export interface ApiEnvelope<T> {
  data: T;
  message: string;
  meta: Record<string, unknown>;
  timestamp: string;
}
