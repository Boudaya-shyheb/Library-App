import { Routes } from '@angular/router';
import { MicroserviceCrudComponent } from './features/microservice-crud/microservice-crud.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'crud/:service', component: MicroserviceCrudComponent },
  { path: '**', redirectTo: 'dashboard' }
];
