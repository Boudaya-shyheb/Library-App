import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { ToastService } from '../../shared/toast.service';

type FieldType = 'text' | 'number' | 'datetime-local' | 'select';
type CrudConfig = {
  title: string;
  listPath: string;
  createPath?: string;
  updatePath?: string;
  updateMethod?: 'put' | 'patch';
  deletePath?: string;
  fields: Array<{
    key: string;
    label: string;
    type?: FieldType;
    relation?: { endpoint: string; valueKey: string; labelKeys: string[] };
    options?: string[];
  }>;
};

@Component({
  selector: 'app-microservice-crud',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="view-header">
      <div class="header-info">
        <h1>{{ config?.title }}</h1>
        <p class="subtitle">Microservice Management Module</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-secondary" (click)="readAll()">
          <span class="btn-icon">🔄</span> Refresh
        </button>
        <button class="btn btn-primary" (click)="startAdd()" [disabled]="!config?.createPath">
          <span class="btn-icon">➕</span> New Entry
        </button>
      </div>
    </div>

    <div class="crud-content">
      <!-- Form Card -->
      <div class="card form-card" [class.active]="editingId || formActive">
        <div class="card-header">
          <h3>{{ editingId ? 'Update Record' : 'Create New' }}</h3>
          <p class="card-subtitle">Complete the fields below to {{ editingId ? 'update' : 'create' }} the entry</p>
        </div>
        <div class="card-body">
          <div class="form-grid">
            <div class="form-group" *ngFor="let f of config?.fields">
              <label>{{ f.label }}</label>
              <ng-container [ngSwitch]="f.type || 'text'">
                <select *ngSwitchCase="'select'" [(ngModel)]="form[f.key]" [name]="f.key">
                  <option value="">-- select --</option>
                  <option *ngFor="let op of getSelectOptions(f.key)" [value]="op.value">{{ op.label }}</option>
                </select>
                <input *ngSwitchDefault [type]="f.type || 'text'" [(ngModel)]="form[f.key]" [name]="f.key" />
              </ng-container>
            </div>
          </div>
        </div>
        <div class="card-footer">
          <button class="btn btn-outline" (click)="cancelEdit()">Cancel</button>
          <button class="btn btn-primary" (click)="save()" [disabled]="!config?.createPath">
            {{ editingId ? 'Confirm Update' : 'Save Record' }}
          </button>
        </div>
      </div>

      <!-- Table Card -->
      <div class="card table-card">
        <div class="table-container">
          <table *ngIf="items.length">
            <thead>
              <tr>
                <th class="id-col">ID</th>
                <th *ngFor="let f of config?.fields">{{ f.label }}</th>
                <th class="action-col">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let item of items">
                <td class="id-cell">{{ getItemId(item) }}</td>
                <td *ngFor="let f of config?.fields">{{ item[f.key] }}</td>
                <td class="row-actions">
                  <button class="icon-btn edit-btn" (click)="editRow(item)" title="Edit">
                    ✏️
                  </button>
                  <button class="icon-btn delete-btn" (click)="deleteRow(item)" [disabled]="!config?.deletePath" title="Delete">
                    🗑️
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div class="empty-state" *ngIf="!items.length">
            <span class="empty-icon">📂</span>
            <p>No records found in this microservice.</p>
          </div>
        </div>
      </div>

      <!-- Debug Card -->
      <div class="card debug-card">
        <div class="card-header" (click)="showDebug = !showDebug">
          <h3>API Communication Log</h3>
          <span class="chevron" [class.up]="showDebug">{{ showDebug ? '▼' : '▶' }}</span>
        </div>
        <div class="card-body" *ngIf="showDebug">
          <pre>{{ responseText || 'Waiting for first request...' }}</pre>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .view-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 32px; }
    .header-info h1 { font-size: 28px; font-weight: 800; margin-bottom: 4px; }
    .subtitle { color: #94a3b8; font-size: 14px; }
    .header-actions { display: flex; gap: 12px; }

    .card { background: rgba(255, 255, 255, 0.03); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 16px; margin-bottom: 24px; }
    .card-header { padding: 20px 24px; border-bottom: 1px solid rgba(255, 255, 255, 0.08); }
    .card-header h3 { font-size: 16px; font-weight: 700; }
    .card-subtitle { font-size: 12px; color: #94a3b8; margin-top: 4px; }
    .card-body { padding: 24px; }
    .card-footer { padding: 16px 24px; background: rgba(0, 0, 0, 0.1); border-top: 1px solid rgba(255, 255, 255, 0.08); display: flex; justify-content: flex-end; gap: 12px; border-radius: 0 0 16px 16px; }

    .form-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
    .form-group label { display: block; font-size: 12px; font-weight: 600; color: #94a3b8; text-transform: uppercase; margin-bottom: 8px; letter-spacing: 0.5px; }
    input, select { width: 100%; background: rgba(15, 23, 42, 0.5); border: 1px solid rgba(255, 255, 255, 0.1); color: white; padding: 10px 14px; border-radius: 8px; outline: none; transition: border-color 0.2s; }
    input:focus, select:focus { border-color: #3b82f6; }

    .btn { padding: 10px 18px; border-radius: 10px; font-weight: 600; font-size: 14px; cursor: pointer; border: none; display: flex; align-items: center; gap: 8px; transition: all 0.2s; }
    .btn-primary { background: #3b82f6; color: white; }
    .btn-primary:hover { background: #2563eb; transform: translateY(-1px); }
    .btn-secondary { background: rgba(255, 255, 255, 0.08); color: white; }
    .btn-outline { background: transparent; border: 1px solid rgba(255, 255, 255, 0.2); color: white; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }

    .table-container { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { padding: 16px 24px; text-align: left; font-size: 11px; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px; border-bottom: 1px solid rgba(255, 255, 255, 0.08); }
    td { padding: 16px 24px; font-size: 14px; border-bottom: 1px solid rgba(255, 255, 255, 0.05); }
    tr:hover td { background: rgba(255, 255, 255, 0.02); }
    .id-col { width: 60px; }
    .id-cell { font-family: monospace; color: #3b82f6; font-weight: 600; }
    .action-col { text-align: right; width: 120px; }
    .row-actions { display: flex; justify-content: flex-end; gap: 10px; }
    .icon-btn { width: 34px; height: 34px; border-radius: 8px; background: rgba(255, 255, 255, 0.05); border: 1px solid rgba(255, 255, 255, 0.1); cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 14px; transition: all 0.2s; }
    .icon-btn:hover { background: rgba(255, 255, 255, 0.1); transform: scale(1.05); }

    .empty-state { padding: 60px; text-align: center; color: #94a3b8; }
    .empty-icon { font-size: 40px; display: block; margin-bottom: 16px; opacity: 0.5; }

    .debug-card .card-header { cursor: pointer; display: flex; justify-content: space-between; align-items: center; }
    .debug-card pre { background: #000; color: #10b981; padding: 20px; border-radius: 12px; font-size: 13px; line-height: 1.6; margin: 0; }
    .chevron { transition: transform 0.3s; }
    .chevron.up { transform: rotate(0deg); }
  `]
})
export class MicroserviceCrudComponent {
  config?: CrudConfig;
  form: Record<string, any> = {};
  relationOptions: Record<string, Array<{ value: string; label: string }>> = {};
  items: any[] = [];
  editingId: string = '';
  responseText = '';
  formActive = false;
  showDebug = false;

  private readonly configs: Record<string, CrudConfig> = {
    inventaire: {
      title: 'Inventaire - Books',
      listPath: '/api/inventaire/books?page=0&size=50',
      createPath: '/api/inventaire/books',
      updatePath: '/api/inventaire/books/{id}',
      deletePath: '/api/inventaire/books/{id}',
      fields: [
        { key: 'title', label: 'Title' },
        { key: 'author', label: 'Author' },
        { key: 'isbn', label: 'ISBN' },
        { key: 'category', label: 'Category' },
        { key: 'publicationYear', label: 'Publication Year', type: 'number' }
      ]
    },
    fournisseurs: {
      title: 'Fournisseur',
      listPath: '/api/fournisseurs?page=0&size=50',
      createPath: '/api/fournisseurs',
      updatePath: '/api/fournisseurs/{id}',
      deletePath: '/api/fournisseurs/{id}',
      fields: [
        { key: 'nom', label: 'Nom' },
        { key: 'email', label: 'Email' },
        { key: 'telephone', label: 'Telephone' },
        { key: 'adresse', label: 'Adresse' }
      ]
    },
    spaces: {
      title: 'Space Service',
      listPath: '/api/spaces',
      createPath: '/api/spaces',
      updatePath: '/api/spaces/{id}',
      deletePath: '/api/spaces/{id}',
      fields: [
        { key: 'name', label: 'Space Name' },
        { key: 'type', label: 'Type', type: 'select', options: ['ROOM', 'STUDY_AREA', 'LAB', 'CONFERENCE_ROOM', 'QUIET_ZONE'] },
        { key: 'capacity', label: 'Capacity', type: 'number' },
        { key: 'location', label: 'Location' },
        { key: 'description', label: 'Description' },
        { key: 'availabilityStatus', label: 'Status', type: 'select', options: ['AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'RESERVED', 'CLOSED'] },
        { key: 'equipment', label: 'Equipment' }
      ]
    },
    reservations: {
      title: 'Reservation Service',
      listPath: '/api/reservations',
      createPath: '/api/reservations',
      updatePath: '/api/reservations/{id}/confirm',
      updateMethod: 'patch',
      fields: [
        { key: 'spaceId', label: 'Space', type: 'select', relation: { endpoint: '/api/spaces', valueKey: 'id', labelKeys: ['name', 'location'] } },
        { key: 'userId', label: 'User ID' },
        { key: 'userFullName', label: 'User Full Name' },
        { key: 'startTime', label: 'Start Time', type: 'datetime-local' },
        { key: 'endTime', label: 'End Time', type: 'datetime-local' },
        { key: 'purpose', label: 'Purpose' }
      ]
    },
    emprunts: {
      title: 'Emprunt Service',
      listPath: '/api/emprunts',
      createPath: '/api/emprunts',
      updatePath: '/api/emprunts/{id}',
      deletePath: '/api/emprunts/{id}',
      fields: [
        { key: 'userId', label: 'User ID' },
        { key: 'documentId', label: 'Document ID', type: 'number' },
        { key: 'documentType', label: 'Document Type' },
        { key: 'dueDate', label: 'Due Date', type: 'datetime-local' },
        { key: 'notes', label: 'Notes' }
      ]
    },
    reclamations: {
      title: 'Reclamation Service',
      listPath: '/api/reclamations',
      createPath: '/api/reclamations',
      updatePath: '/api/reclamations/{id}',
      deletePath: '/api/reclamations/{id}',
      fields: [
        { key: 'userId', label: 'User ID' },
        { key: 'sujet', label: 'Subject' },
        { key: 'description', label: 'Description' },
        { key: 'statut', label: 'Status', type: 'select', options: ['OUVERTE', 'EN_COURS', 'RESOLUE'] }
      ]
    },
    forumTopics: {
      title: 'Forum Topics',
      listPath: '/api/forum/topics',
      createPath: '/api/forum/topics',
      updatePath: '/api/forum/topics/{id}',
      deletePath: '/api/forum/topics/{id}',
      fields: [
        { key: 'title', label: 'Title' },
        { key: 'description', label: 'Description' },
        { key: 'authorId', label: 'Author ID', type: 'number' }
      ]
    },
    forumPosts: {
      title: 'Forum Posts',
      listPath: '/api/forum/posts',
      createPath: '/api/forum/posts',
      updatePath: '/api/forum/posts/{id}',
      deletePath: '/api/forum/posts/{id}',
      fields: [
        { key: 'topicId', label: 'Topic ID', type: 'number' },
        { key: 'authorId', label: 'Author ID', type: 'number' },
        { key: 'content', label: 'Content' }
      ]
    }
  };

  constructor(private route: ActivatedRoute, private api: ApiService, private toast: ToastService) {
    this.route.paramMap.subscribe(params => {
      const key = params.get('service') ?? 'inventaire';
      this.config = this.configs[key] ?? this.configs['inventaire'];
      this.form = {};
      this.config.fields.forEach(f => this.form[f.key] = '');
      this.editingId = '';
      this.responseText = '';
      this.items = [];
      this.relationOptions = {};
      this.loadRelationOptions();
      this.readAll();
    });
  }

  readAll(): void {
    if (!this.config) return;
    this.api.get<unknown>(this.config.listPath).subscribe({
      next: res => {
        this.responseText = JSON.stringify(res, null, 2);
        this.items = this.extractList(res);
      },
      error: err => this.fail(err)
    });
  }

  save(): void {
    if (!this.config?.createPath) return;
    const body = this.buildBody();
    if (!this.editingId) {
      this.api.post<unknown>(this.path(this.config.createPath), body).subscribe({
        next: res => {
          this.ok('Create OK');
          this.responseText = JSON.stringify(res, null, 2);
          this.readAll();
          this.startAdd();
        },
        error: err => this.fail(err)
      });
      return;
    }
    if (!this.config.updatePath) {
      this.toast.show('error', 'Update not available for this service');
      return;
    }
    const updatePath = this.path(this.config.updatePath, this.editingId);
    const request = (this.config.updateMethod || 'put') === 'patch'
      ? this.api.patch<unknown>(updatePath, Object.keys(body).length ? body : null)
      : this.api.put<unknown>(updatePath, body);
    request.subscribe({
      next: res => {
        this.ok('Update OK');
        this.responseText = JSON.stringify(res, null, 2);
        this.readAll();
        this.startAdd();
      },
      error: err => this.fail(err)
    });
  }

  startAdd(): void {
    this.editingId = '';
    if (!this.config) return;
    this.config.fields.forEach(f => this.form[f.key] = '');
  }

  cancelEdit(): void {
    this.startAdd();
  }

  editRow(item: any): void {
    if (!this.config) return;
    this.editingId = this.getItemId(item);
    this.config.fields.forEach(f => {
      this.form[f.key] = item[f.key] ?? '';
    });
  }

  deleteRow(item: any): void {
    if (!this.config?.deletePath) return;
    const id = this.getItemId(item);
    this.api.delete<unknown>(this.path(this.config.deletePath, id)).subscribe({
      next: res => {
        this.ok('Delete OK');
        this.responseText = JSON.stringify(res, null, 2);
        this.readAll();
      },
      error: err => this.fail(err)
    });
  }

  getItemId(item: any): string {
    return String(item?.id ?? item?.bookId ?? item?.topicId ?? '');
  }

  getSelectOptions(fieldKey: string): Array<{ value: string; label: string }> {
    return this.relationOptions[fieldKey] ?? [];
  }

  private loadRelationOptions(): void {
    if (!this.config) return;
    this.config.fields.forEach(field => {
      if (field.options?.length) {
        this.relationOptions[field.key] = field.options.map(op => ({ value: op, label: op }));
        return;
      }
      if (!field.relation) return;
      this.api.get<any>(field.relation.endpoint).subscribe({
        next: res => {
          const list = this.extractList(res);
          const map = new Map<string, { value: string; label: string }>();
          list.forEach((it: any) => {
            const value = String(it[field.relation!.valueKey] ?? '');
            if (!value) return;
            const label = field.relation!.labelKeys.map(k => String(it[k] ?? '')).filter(Boolean).join(' - ') || value;
            if (!map.has(value)) map.set(value, { value, label });
          });
          this.relationOptions[field.key] = Array.from(map.values());
        },
        error: () => {
          this.relationOptions[field.key] = [];
        }
      });
    });
  }

  private extractList(res: any): any[] {
    if (Array.isArray(res)) return res;
    // Handle ApiResponse wrapper
    if (res && res.data) {
      if (Array.isArray(res.data)) return res.data;
      if (Array.isArray(res.data.content)) return res.data.content; // Spring Page
    }
    if (res && Array.isArray(res.content)) return res.content;
    return [];
  }

  private path(template: string, idOverride?: string): string {
    const id = idOverride || this.editingId || '1';
    const extra = String(this.form['topicId'] ?? this.form['spaceId'] ?? '1');
    return template.replace('{id}', id).replace('{extra}', extra);
  }

  private buildBody(): Record<string, any> {
    const out: Record<string, any> = {};
    if (!this.config) return out;
    this.config.fields.forEach(f => {
      let raw = this.form[f.key];
      if (raw === '' || raw === null || raw === undefined) return;
      
      if (f.type === 'number') {
        out[f.key] = Number(raw);
      } else if (f.type === 'datetime-local') {
        // Spring LocalDateTime usually requires T00:00:00 format
        if (typeof raw === 'string' && raw.length === 16) {
          out[f.key] = raw + ':00';
        } else {
          out[f.key] = raw;
        }
      } else {
        out[f.key] = raw;
      }
    });
    
    // Custom logic for specific services if needed
    if (this.config.title === 'Forum Posts') {
      // Any special mappings here
    }
    return out;
  }

  private ok(message: string): void {
    this.toast.show('success', message);
  }

  private fail(err: any): void {
    this.toast.show('error', 'Request failed');
    this.responseText = JSON.stringify(err?.error ?? err, null, 2);
  }
}
