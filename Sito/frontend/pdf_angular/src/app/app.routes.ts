import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { AdminDashboard } from './pages/admin-dashboard/admin-dashboard';
import { Lobby } from './pages/lobby/lobby';
import { AdminPartita } from './pages/admin-partita/admin-partita';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'lobby/:id', component: Lobby },
  { path: 'admin', component: AdminDashboard },
  { path: 'admin/partita/:id', component: AdminPartita },
];