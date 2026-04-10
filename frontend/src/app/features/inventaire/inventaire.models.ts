export interface Book {
  bookId?: number;
  title: string;
  author: string;
  isbn: string;
  category: string;
  publicationYear: number;
  quantity?: number;
}

export interface ApiEnvelope<T> {
  data: T;
  message: string;
  meta: Record<string, unknown>;
  timestamp: string;
}
