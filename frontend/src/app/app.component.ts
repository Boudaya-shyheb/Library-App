import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ToastComponent } from './shared/toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, ToastComponent, CommonModule],
  template: `
    <div class="dashboard-wrapper">
      <!-- Sidebar -->
      <aside class="sidebar">
        <div class="logo-section">
          <span class="logo-icon">📚</span>
          <span class="logo-text">LibAdmin</span>
        </div>
        
        <nav class="nav-list">
          <div class="nav-label">Management</div>
          <a routerLink="/crud/spaces" routerLinkActive="active" class="nav-item">
            <span class="icon">🏢</span>
            <span>Spaces</span>
          </a>
          <a routerLink="/crud/reservations" routerLinkActive="active" class="nav-item">
            <span class="icon">📅</span>
            <span>Reservations</span>
          </a>
          <a routerLink="/crud/emprunts" routerLinkActive="active" class="nav-item">
            <span class="icon">🔄</span>
            <span>Emprunts</span>
          </a>
          <a routerLink="/crud/inventaire" routerLinkActive="active" class="nav-item">
            <span class="icon">📦</span>
            <span>Inventaire</span>
          </a>
          
          <div class="nav-label">Community</div>
          <a routerLink="/crud/forumTopics" routerLinkActive="active" class="nav-item">
            <span class="icon">💬</span>
            <span>Forum Topics</span>
          </a>
          <a routerLink="/crud/forumPosts" routerLinkActive="active" class="nav-item">
            <span class="icon">📝</span>
            <span>Forum Posts</span>
          </a>
          
          <div class="nav-label">Operations</div>
          <a routerLink="/crud/fournisseurs" routerLinkActive="active" class="nav-item">
            <span class="icon">🚚</span>
            <span>Fournisseurs</span>
          </a>
          <a routerLink="/crud/reclamations" routerLinkActive="active" class="nav-item">
            <span class="icon">⚠️</span>
            <span>Reclamations</span>
          </a>
        </nav>

        <div class="sidebar-footer">
          <div class="status-indicator">
            <span class="pulse"></span>
            System Online
          </div>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="main-content">
        <header class="top-bar">
          <div class="gateway-info">
            <span class="label">API Gateway:</span>
            <span class="val">localhost:8080</span>
          </div>
          <div class="user-id">
            Admin Mode
          </div>
        </header>
        
        <div class="view-wrapper">
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
    <app-toast></app-toast>
  `,
  styles: [`
    :host {
      --bg-dark: #0f172a;
      --sidebar-width: 260px;
      --accent: #3b82f6;
      --glass: rgba(255, 255, 255, 0.05);
      --glass-border: rgba(255, 255, 255, 0.1);
      --text-main: #f8fafc;
      --text-muted: #94a3b8;
    }

    .dashboard-wrapper {
      display: flex;
      height: 100vh;
      background-color: var(--bg-dark);
      color: var(--text-main);
      font-family: 'Inter', system-ui, -apple-system, sans-serif;
    }

    /* Sidebar */
    .sidebar {
      width: var(--sidebar-width);
      background: rgba(15, 23, 42, 0.95);
      border-right: 1px solid var(--glass-border);
      display: flex;
      flex-direction: column;
      backdrop-filter: blur(12px);
    }

    .logo-section {
      padding: 32px 24px;
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .logo-icon { font-size: 24px; }
    .logo-text { font-size: 20px; font-weight: 800; letter-spacing: -0.5px; }

    .nav-list {
      flex: 1;
      padding: 0 12px;
    }

    .nav-label {
      padding: 24px 12px 8px;
      font-size: 11px;
      font-weight: 700;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 1px;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      text-decoration: none;
      color: var(--text-muted);
      border-radius: 8px;
      margin-bottom: 2px;
      transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
      font-weight: 500;
    }

    .nav-item:hover {
      background: var(--glass);
      color: var(--text-main);
    }

    .nav-item.active {
      background: rgba(59, 130, 246, 0.15);
      color: var(--accent);
      box-shadow: inset 0 0 0 1px rgba(59, 130, 246, 0.2);
    }

    .sidebar-footer {
      padding: 24px;
      border-top: 1px solid var(--glass-border);
    }

    .status-indicator {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 12px;
      color: var(--text-muted);
    }

    .pulse {
      width: 8px;
      height: 8px;
      background: #22c55e;
      border-radius: 50%;
      box-shadow: 0 0 0 rgba(34, 197, 94, 0.4);
      animation: pulse 2s infinite;
    }

    @keyframes pulse {
      0% { box-shadow: 0 0 0 0 rgba(34, 197, 94, 0.7); }
      70% { box-shadow: 0 0 0 10px rgba(34, 197, 94, 0); }
      100% { box-shadow: 0 0 0 0 rgba(34, 197, 94, 0); }
    }

    /* Main Content */
    .main-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }

    .top-bar {
      height: 72px;
      padding: 0 32px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid var(--glass-border);
      background: rgba(15, 23, 42, 0.5);
    }

    .gateway-info { border: 1px solid var(--glass-border); padding: 6px 14px; border-radius: 20px; font-size: 13px; }
    .gateway-info .label { color: var(--text-muted); margin-right: 6px; }
    .gateway-info .val { color: var(--accent); font-weight: 600; }

    .user-id { font-size: 14px; font-weight: 600; color: var(--text-muted); }

    .view-wrapper {
      flex: 1;
      padding: 32px;
      overflow-y: auto;
      background: radial-gradient(circle at 50% 0%, rgba(59, 130, 246, 0.03), transparent 70%);
    }
  `]
})
export class AppComponent {}
