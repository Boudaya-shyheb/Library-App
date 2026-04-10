import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard-home">
      <div class="welcome-header">
        <h1>Library Ecosystem Overview</h1>
        <p>Real-time status of your 7 integrated microservices</p>
      </div>

      <div class="stats-grid">
        <!-- Spaces Card -->
        <div class="stat-card blue" routerLink="/crud/spaces">
          <div class="card-icon">🏢</div>
          <div class="card-info">
            <span class="label">Total Spaces</span>
            <span class="value">{{ stats.spaces }}</span>
          </div>
        </div>

        <!-- Reservations Card -->
        <div class="stat-card purple" routerLink="/crud/reservations">
          <div class="card-icon">📅</div>
          <div class="card-info">
            <span class="label">Reservations</span>
            <span class="value">{{ stats.reservations }}</span>
          </div>
        </div>

        <!-- Inventory Card -->
        <div class="stat-card green" routerLink="/crud/inventaire">
          <div class="card-icon">📦</div>
          <div class="card-info">
            <span class="label">Books in Stock</span>
            <span class="value">{{ stats.inventaire }}</span>
          </div>
        </div>

        <!-- Loans Card -->
        <div class="stat-card orange" routerLink="/crud/emprunts">
          <div class="card-icon">🔄</div>
          <div class="card-info">
            <span class="label">Active Loans</span>
            <span class="value">{{ stats.emprunts }}</span>
          </div>
        </div>
      </div>

      <div class="services-status-card card">
        <div class="card-header">
          <h3>Gateway Routing Table (Port 9000)</h3>
        </div>
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>Service Name</th>
                <th>Gateway Route</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let s of serviceRoutes">
                <td>{{ s.name }}</td>
                <td><code>{{ s.route }}</code></td>
                <td><span class="status-pill">Active</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-home { animation: fadeIn 0.4s ease; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

    .welcome-header { margin-bottom: 32px; }
    .welcome-header h1 { font-size: 28px; font-weight: 800; }
    .welcome-header p { color: #94a3b8; }

    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 24px; margin-bottom: 40px; }
    
    .stat-card { 
      background: rgba(255, 255, 255, 0.03); 
      border: 1px solid rgba(255, 255, 255, 0.08); 
      padding: 24px; 
      border-radius: 20px; 
      display: flex; 
      align-items: center; 
      gap: 20px; 
      cursor: pointer;
      transition: all 0.3s;
    }
    .stat-card:hover { transform: translateY(-4px); background: rgba(255, 255, 255, 0.06); border-color: rgba(255, 255, 255, 0.2); }
    
    .card-icon { width: 56px; height: 56px; border-radius: 16px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
    .blue .card-icon { background: rgba(59, 130, 246, 0.1); color: #3b82f6; }
    .purple .card-icon { background: rgba(168, 85, 247, 0.1); color: #a855f7; }
    .green .card-icon { background: rgba(34, 197, 94, 0.1); color: #22c55e; }
    .orange .card-icon { background: rgba(249, 115, 22, 0.1); color: #f97316; }

    .card-info { display: flex; flex-direction: column; }
    .label { font-size: 13px; color: #94a3b8; font-weight: 500; }
    .value { font-size: 24px; font-weight: 800; margin-top: 2px; }

    .card { background: rgba(255, 255, 255, 0.03); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 20px; }
    .card-header { padding: 24px; border-bottom: 1px solid rgba(255, 255, 255, 0.08); }
    .card-header h3 { font-size: 16px; font-weight: 700; }

    table { width: 100%; border-collapse: collapse; }
    th { text-align: left; padding: 16px 24px; font-size: 11px; color: #94a3b8; text-transform: uppercase; border-bottom: 1px solid rgba(255, 255, 255, 0.08); }
    td { padding: 16px 24px; font-size: 14px; border-bottom: 1px solid rgba(255, 255, 255, 0.05); }
    code { background: rgba(255, 255, 255, 0.08); padding: 4px 8px; border-radius: 6px; font-family: monospace; color: #3b82f6; }
    
    .status-pill { background: rgba(34, 197, 94, 0.15); color: #22c55e; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; }
  `]
})
export class DashboardComponent implements OnInit {
  stats = {
    spaces: 0,
    reservations: 0,
    emprunts: 0,
    inventaire: 0
  };

  serviceRoutes = [
    { name: 'Space Service', route: '/api/spaces/**' },
    { name: 'Reservation Service', route: '/api/reservations/**' },
    { name: 'Emprunt Service', route: '/api/emprunts/**' },
    { name: 'Inventaire Service', route: '/api/inventaire/**' },
    { name: 'Forum Service', route: '/api/forum/**' },
    { name: 'Fournisseur Service', route: '/api/fournisseurs/**' },
    { name: 'Reclamation Service', route: '/api/reclamations/**' }
  ];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadStats();
  }

  private loadStats(): void {
    // Attempt to load some quick counts
    this.api.get<any[]>('/api/spaces').subscribe(res => this.stats.spaces = this.extractCount(res));
    this.api.get<any[]>('/api/reservations').subscribe(res => this.stats.reservations = this.extractCount(res));
    this.api.get<any[]>('/api/emprunts').subscribe(res => this.stats.emprunts = this.extractCount(res));
    this.api.get<any[]>('/api/inventaire/books').subscribe(res => this.stats.inventaire = this.extractCount(res));
  }

  private extractCount(res: any): number {
    if (Array.isArray(res)) return res.length;
    if (Array.isArray(res?.data)) return res.data.length;
    if (res?.data?.totalElements !== undefined) return res.data.totalElements;
    return 0;
  }
}
